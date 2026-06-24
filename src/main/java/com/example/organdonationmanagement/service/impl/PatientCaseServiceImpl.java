
package com.example.organdonationmanagement.service.impl;
import com.example.organdonationmanagement.dto.request.PatientCaseRequest;
import com.example.organdonationmanagement.entity.*;
import com.example.organdonationmanagement.entity.enums.*;
import com.example.organdonationmanagement.exception.ResourceNotFoundException;
import com.example.organdonationmanagement.repository.*;
import com.example.organdonationmanagement.service.PatientCaseService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientCaseServiceImpl implements PatientCaseService {

    private final PatientCaseRepository patientCaseRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<PatientCase> getAll() {
        User user = getCurrentUser();
        if (user.getRole() == Role.ADMIN) return patientCaseRepository.findAll();
        return patientCaseRepository.findByHospitalId(user.getHospital().getId());
    }

    @Override
    public PatientCase getById(Long id) {
        User user = getCurrentUser();
        PatientCase patientCase = patientCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient case not found"));

        if (user.getRole() == Role.STAFF && !patientCase.getHospital().getId().equals(user.getHospital().getId())) {
            throw new RuntimeException("Bạn không có quyền truy cập dữ liệu bệnh viện khác");
        }
        return patientCase;
    }

    @Override
    public PatientCase create(PatientCaseRequest request) {
        User user = getCurrentUser();

        Hospital hospital;
        if (user.getRole() == Role.ADMIN && request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        } else {
            hospital = user.getHospital();
        }

        if (hospital == null) {
            throw new RuntimeException("Tài khoản của bạn chưa được liên kết với bệnh viện nào.");
        }

        String generatedCaseCode;
        boolean isDuplicate;
        do {
            int randomNum = (int)(Math.random() * 90000) + 10000;
            generatedCaseCode = "CASE" + randomNum;
            isDuplicate = patientCaseRepository.existsByCaseCode(generatedCaseCode);
        } while (isDuplicate);
        PatientCase patientCase = PatientCase.builder()
                .caseCode(generatedCaseCode)
                .patientName(request.getPatientName())
                .hospital(hospital)
                .birthYear(request.getBirthYear())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .cause(Cause.valueOf(request.getCause().toUpperCase()))
                .status(PatientStatus.valueOf(request.getStatus().toUpperCase()))
                .donorStatus(DonorStatus.valueOf(request.getDonorStatus().toUpperCase()))
                .note(request.getNote())
                .incidentDate(request.getIncidentDate())
                .createdAt(LocalDateTime.now())
                .bloodtype(request.getBloodtype())
                .build();

        return patientCaseRepository.save(patientCase);
    }
    @Override
    public PatientCase update(Long id, PatientCaseRequest request, User currentUser) {
        PatientCase patientCase = getById(id);

        patientCase.setPatientName(request.getPatientName());
        patientCase.setBirthYear(request.getBirthYear());
        patientCase.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        patientCase.setCause(Cause.valueOf(request.getCause().toUpperCase()));
        patientCase.setStatus(PatientStatus.valueOf(request.getStatus().toUpperCase()));
        patientCase.setDonorStatus(DonorStatus.valueOf(request.getDonorStatus().toUpperCase()));
        patientCase.setNote(request.getNote());
        patientCase.setIncidentDate(request.getIncidentDate());
        patientCase.setBloodtype(request.getBloodtype());
        patientCase.setUpdatedAt(LocalDateTime.now());
        if (currentUser.getRole() == Role.ADMIN) {
            Hospital newHospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bệnh viện không tồn tại"));
            patientCase.setHospital(newHospital);
        } else {
            if (currentUser.getHospital() == null) {
                throw new RuntimeException("Tài khoản nhân viên chưa được gán bệnh viện!");
            }
            patientCase.setHospital(currentUser.getHospital());
        }

        // 4. Lưu lại
        return patientCaseRepository.save(patientCase);
    }

    @Override
    public void delete(Long id) {
        patientCaseRepository.delete(getById(id));
    }

    @Override
    public List<PatientCase> search(Long hospitalId, String status, LocalDate fromDate, LocalDate toDate) {
        User user = getCurrentUser();
        Long finalHospitalId = (user.getRole() == Role.STAFF) ? user.getHospital().getId() : hospitalId;

        Specification<PatientCase> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (finalHospitalId != null) {
                predicates.add(cb.equal(root.get("hospital").get("id"), finalHospitalId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), PatientStatus.valueOf(status.toUpperCase())));
            }
            if (fromDate != null && toDate != null) {
                predicates.add(cb.between(root.get("incidentDate"), fromDate, toDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return patientCaseRepository.findAll(specification);
    }

    @Override
    public Page<PatientCase> findPatients(Long hospitalId, String status, LocalDate fromDate, LocalDate toDate, int page, int size) {
        User user = getCurrentUser();
        Long finalHospitalId = (user.getRole() == Role.STAFF) ? user.getHospital().getId() : hospitalId;

        Pageable pageable = PageRequest.of(page, size, Sort.by("incidentDate").descending());
        PatientStatus patientStatus = (status != null && !status.isEmpty()) ? PatientStatus.valueOf(status) : null;

        return patientCaseRepository.searchPatients(finalHospitalId, patientStatus, fromDate, toDate, pageable);
    }
}

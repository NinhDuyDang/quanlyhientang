package com.example.organdonationmanagement.service.impl;

import com.example.organdonationmanagement.dto.request.HospitalRequest;
import com.example.organdonationmanagement.entity.Hospital;
import com.example.organdonationmanagement.exception.ResourceNotFoundException;
import com.example.organdonationmanagement.repository.HospitalRepository;
import com.example.organdonationmanagement.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;

    @Override
    public Page<Hospital> getAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return hospitalRepository.searchHospitals(keyword, pageable);
    }

    @Override
    public Hospital getById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh viện yêu cầu"));
    }

    @Override
    public Hospital create(HospitalRequest request) {
        String generatedCode;
        boolean isDuplicate;
        do {
            int randomNum = (int)(Math.random() * 9000) + 1000;
            generatedCode = "BV" + randomNum;
            isDuplicate = hospitalRepository.existsByCode(generatedCode);
        } while (isDuplicate);

        Hospital hospital = Hospital.builder()
                .code(generatedCode)
                .name(request.getName())
                .address(request.getAddress())
                .province(request.getProvince())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        return hospitalRepository.save(hospital);
    }

    @Override
    public Hospital update(Long id, HospitalRequest request) {
        Hospital hospital = getById(id);

        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setProvince(request.getProvince());
        hospital.setPhone(request.getPhone());
        hospital.setEmail(request.getEmail());

        return hospitalRepository.save(hospital);
    }

    @Override
    public void delete(Long id) {
        hospitalRepository.delete(getById(id));
    }
    @Override
    public List<Hospital> findAll() {
        return hospitalRepository.findAll();
    }
}

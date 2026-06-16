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

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;

    @Override
    public Page<Hospital> getAll(String keyword, int page, int size) {
        // Cấu hình phân trang (Gom nhóm sắp xếp theo ID giảm dần hoặc createdAt giảm dần tùy thuộc thuộc tính DB của bạn)
        // Lưu ý: Nếu Entity Hospital của bạn không có trường "createdAt", hãy sửa "createdAt" thành "id" để tránh lỗi sập hệ thống nhé!
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        // Gọi hàm tìm kiếm thông minh (Tự nhận diện từ khóa trống hoặc có giá trị)
        return hospitalRepository.searchHospitals(keyword, pageable);
    }

    @Override
    public Hospital getById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh viện yêu cầu"));
    }

    @Override
    public Hospital create(HospitalRequest request) {
        if (hospitalRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Mã bệnh viện này đã tồn tại trên hệ thống!");
        }

        Hospital hospital = Hospital.builder()
                .code(request.getCode())
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
}

package com.example.organdonationmanagement.service;

import com.example.organdonationmanagement.dto.request.HospitalRequest;
import com.example.organdonationmanagement.entity.Hospital;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HospitalService {
    Page<Hospital> getAll(String keyword, int page, int size);
    Hospital getById(Long id);
    Hospital create(HospitalRequest request);
    Hospital update(Long id, HospitalRequest request);
    void delete(Long id);

    List<Hospital> findAll();
}

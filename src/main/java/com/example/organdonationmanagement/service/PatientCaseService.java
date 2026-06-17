package com.example.organdonationmanagement.service;

import com.example.organdonationmanagement.dto.request.PatientCaseRequest;
import com.example.organdonationmanagement.entity.PatientCase;
import java.time.LocalDate;
import java.util.List;

public interface PatientCaseService {
    List<PatientCase> getAll();
    PatientCase getById(Long id);
    PatientCase create(PatientCaseRequest request);
    PatientCase update(Long id, PatientCaseRequest request);
    void delete(Long id);
    List<PatientCase> search(Long hospitalId, String status, LocalDate fromDate, LocalDate toDate);
}
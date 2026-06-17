package com.example.organdonationmanagement.repository;

import com.example.organdonationmanagement.entity.PatientCase;
import com.example.organdonationmanagement.entity.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientCaseRepository extends JpaRepository<PatientCase, Long>, JpaSpecificationExecutor<PatientCase> {
    
    long countByStatus(PatientStatus status);
    
    @Query("SELECT p.hospital.name, COUNT(p) FROM PatientCase p GROUP BY p.hospital.name")
    List<Object[]> countCasesByHospital();
    
    @Query("SELECT p.status, COUNT(p) FROM PatientCase p GROUP BY p.status")
    List<Object[]> countCasesByStatusGroup();
    
    @Query(value = "SELECT MONTH(created_at) as month, COUNT(id) as count " +
            "FROM patient_cases " +
            "WHERE YEAR(created_at) = 2026 " +
            "GROUP BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> countCasesByMonthInCurrentYear();
}
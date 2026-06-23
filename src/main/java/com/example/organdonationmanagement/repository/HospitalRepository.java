package com.example.organdonationmanagement.repository;

import com.example.organdonationmanagement.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    boolean existsByCode(String code);


    @Query(value = "SELECT code FROM hospitals ORDER BY code DESC LIMIT 1", nativeQuery = true)
    String findLastCode();

    @Query("SELECT h FROM Hospital h WHERE :keyword IS NULL OR :keyword = '' " +
            "OR LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(h.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(h.province) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Hospital> searchHospitals(@Param("keyword") String keyword, Pageable pageable);
}

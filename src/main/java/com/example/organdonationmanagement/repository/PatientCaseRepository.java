//package com.example.organdonationmanagement.repository;
//
//import com.example.organdonationmanagement.entity.PatientCase;
//import com.example.organdonationmanagement.entity.enums.PatientStatus;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface PatientCaseRepository extends JpaRepository<PatientCase, Long>, JpaSpecificationExecutor<PatientCase> {
//
//    boolean existsByCaseCode(String caseCode);
//    long countByStatus(PatientStatus status);
//
//    @Query("SELECT p FROM PatientCase p WHERE " +
//            "(:hospitalId IS NULL OR p.hospital.id = :hospitalId) AND " +
//            "(:status IS NULL OR p.status = :status) AND " +
//            "(:fromDate IS NULL OR p.incidentDate >= :fromDate) AND " +
//            "(:toDate IS NULL OR p.incidentDate <= :toDate)")
//    Page<PatientCase> searchPatients(
//            @Param("hospitalId") Long hospitalId,
//            @Param("status") PatientStatus status,
//            @Param("fromDate") LocalDate fromDate,
//            @Param("toDate") LocalDate toDate,
//            Pageable pageable);
//
//
//    @Query("SELECT p.hospital.name, COUNT(p) FROM PatientCase p GROUP BY p.hospital.name")
//    List<Object[]> countCasesByHospital();
//
//    @Query("SELECT p.status, COUNT(p) FROM PatientCase p GROUP BY p.status")
//    List<Object[]> countCasesByStatusGroup();
//
//    @Query(value = "SELECT MONTH(created_at) as month, COUNT(id) as count FROM patient_cases WHERE YEAR(created_at) = 2026 GROUP BY MONTH(created_at)", nativeQuery = true)
//    List<Object[]> countCasesByMonthInCurrentYear();
//}


package com.example.organdonationmanagement.repository;

import com.example.organdonationmanagement.entity.PatientCase;
import com.example.organdonationmanagement.entity.enums.DonorStatus;
import com.example.organdonationmanagement.entity.enums.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientCaseRepository extends JpaRepository<PatientCase, Long>, JpaSpecificationExecutor<PatientCase> {

    boolean existsByCaseCode(String caseCode);
    long countByStatus(PatientStatus status);
    List<PatientCase> findByHospitalId(Long hospitalId);
    @Query("SELECT p FROM PatientCase p WHERE " +
            "(:hospitalId IS NULL OR p.hospital.id = :hospitalId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:fromDate IS NULL OR p.incidentDate >= :fromDate) AND " +
            "(:toDate IS NULL OR p.incidentDate <= :toDate)")
    Page<PatientCase> searchPatients(
            @Param("hospitalId") Long hospitalId,
            @Param("status") PatientStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);
    @Query("SELECT p.hospital.name, COUNT(p) FROM PatientCase p GROUP BY p.hospital.name")
    List<Object[]> countCasesByHospital();

    @Query("SELECT p.status, COUNT(p) FROM PatientCase p GROUP BY p.status")
    List<Object[]> countCasesByStatusGroup();

    @Query(value = "SELECT MONTH(created_at) as month, COUNT(id) as count FROM patient_cases WHERE YEAR(created_at) = 2026 GROUP BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> countCasesByMonthInCurrentYear();

    @Query("SELECT p.status, COUNT(p) FROM PatientCase p WHERE p.hospital.id = :hospitalId GROUP BY p.status")
    List<Object[]> countCasesByStatusByHospital(@Param("hospitalId") Long hospitalId);

    @Query(value = "SELECT MONTH(created_at) as month, COUNT(id) as count FROM patient_cases " +
            "WHERE YEAR(created_at) = 2026 AND hospital_id = :hospitalId " +
            "GROUP BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> countCasesByMonthByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT count(p) FROM PatientCase p WHERE p.donorStatus = :status")
    long countByDonorStatus(DonorStatus status);
    // Sửa lại hàm này trong PatientCaseRepository
    @Query("SELECT count(p) FROM PatientCase p WHERE p.donorStatus = :status AND (:hId IS NULL OR p.hospital.id = :hId)")
    long countByDonorStatusAndHospitalId(@Param("status") DonorStatus status, @Param("hId") Long hId);
}

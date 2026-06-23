////package com.example.organdonationmanagement.service.impl;
////
////import com.example.organdonationmanagement.entity.User;
////import com.example.organdonationmanagement.entity.enums.DonorStatus;
////import com.example.organdonationmanagement.entity.enums.PatientStatus;
////import com.example.organdonationmanagement.repository.PatientCaseRepository;
////import com.example.organdonationmanagement.service.DashboardService;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////import java.util.HashMap;
////import java.util.LinkedHashMap;
////import java.util.List;
////import java.util.Map;
////
////@Service
////public class DashboardServiceImpl implements DashboardService {
////
////    @Autowired
////    private PatientCaseRepository patientCaseRepository;
////
////
//////    @Override
//////    public Map<String, Long> getDashboardMetrics() {
//////        Map<String, Long> metrics = new HashMap<>();
//////        metrics.put("totalCases", patientCaseRepository.count());
//////        metrics.put("riskCases", patientCaseRepository.countByStatus(PatientStatus.BRAIN_DEATH_1));
//////        metrics.put("confirmedCases", patientCaseRepository.countByStatus(PatientStatus.BRAIN_DEATH_2));
//////        metrics.put("notEligibleCases", patientCaseRepository.countByStatus(PatientStatus.BRAIN_DEATH_3));
//////        return metrics;
//////    }
////
////    public Map<String, Long> getDashboardMetrics() {
////        Map<String, Long> metrics = new HashMap<>();
////
////        // Giả sử bạn có hàm lấy user đang đăng nhập
////        User currentUser = getCurrentUser();
////
////        if (currentUser.getRole().name().equals("ADMIN")) {
////            metrics.put("riskCases", patientCaseRepository.countByDonorStatus(DonorStatus.POTENTIAL));
////            metrics.put("confirmedCases", patientCaseRepository.countByDonorStatus(DonorStatus.FAMILY_ACCEPTED));
////            metrics.put("notEligibleCases", patientCaseRepository.countByDonorStatus(DonorStatus.FAMILY_REJECTED));
////        } else {
////            // STAFF chỉ thấy số liệu bệnh viện của mình
////            Long hospitalId = currentUser.getHospital().getId();
////            metrics.put("riskCases", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.POTENTIAL, hospitalId));
////            metrics.put("confirmedCases", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.FAMILY_ACCEPTED, hospitalId));
////            metrics.put("notEligibleCases", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.FAMILY_REJECTED, hospitalId));
////        }
////
////        return metrics;
////    }
////
////
////    @Override
////    public int[] getMonthlyTrendData() {
////        int[] monthlyTrend = new int[12];
////        List<Object[]> results = patientCaseRepository.countCasesByMonthInCurrentYear();
////        for (Object[] result : results) {
////            int month = ((Number) result[0]).intValue();
////            int count = ((Number) result[1]).intValue();
////            if (month >= 1 && month <= 12) {
////                monthlyTrend[month - 1] = count;
////            }
////        }
////        return monthlyTrend;
////    }
////
////
////    @Override
////    public Map<String, Long> getHospitalChartData() {
////        Map<String, Long> hospitalData = new LinkedHashMap<>();
////        List<Object[]> results = patientCaseRepository.countCasesByHospital();
////        for (Object[] result : results) {
////            String hospitalName = (String) result[0];
////            Long count = ((Number) result[1]).longValue();
////            hospitalData.put(hospitalName != null ? hospitalName : "Chưa rõ BV", count);
////        }
////        return hospitalData;
////    }
////    @Override
////    public Map<String, Long> getStatusPieChartData() {
////        Map<String, Long> statusData = new HashMap<>();
////        List<Object[]> results = patientCaseRepository.countCasesByStatusGroup();
////        for (Object[] result : results) {
////            PatientStatus status = (PatientStatus) result[0];
////            Long count = ((Number) result[1]).longValue();
////            if (status != null) {
////                statusData.put(status.name(), count);
////            }
////        }
////        return statusData;
////    }
////}
//package com.example.organdonationmanagement.service.impl;
//
//import com.example.organdonationmanagement.entity.User;
//import com.example.organdonationmanagement.entity.enums.DonorStatus;
//import com.example.organdonationmanagement.entity.enums.PatientStatus;
//import com.example.organdonationmanagement.repository.PatientCaseRepository;
//import com.example.organdonationmanagement.repository.UserRepository;
//import com.example.organdonationmanagement.service.DashboardService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class DashboardServiceImpl implements DashboardService {
//
//    @Autowired
//    private PatientCaseRepository patientCaseRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User getCurrentUser() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = (principal instanceof UserDetails) ?
//                ((UserDetails) principal).getUsername() : principal.toString();
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//    @Override
//    public Map<String, Long> getDashboardMetrics() {
//        Map<String, Long> metrics = new HashMap<>();
//        User currentUser = getCurrentUser();
//
//        if ("ADMIN".equals(currentUser.getRole().name())) {
//            metrics.put("riskCases", patientCaseRepository.countByDonorStatus(DonorStatus.POTENTIAL));
//            metrics.put("confirmedCases", patientCaseRepository.countByDonorStatus(DonorStatus.FAMILY_ACCEPTED));
//            metrics.put("notEligibleCases", patientCaseRepository.countByDonorStatus(DonorStatus.FAMILY_REJECTED));
//        } else if (currentUser.getHospital() != null) {
//            Long hospitalId = currentUser.getHospital().getId();
//            metrics.put("riskCases", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.POTENTIAL, hospitalId));
//            metrics.put("confirmedCases", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.FAMILY_ACCEPTED, hospitalId));
//            metrics.put("notEligibleCases", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.FAMILY_REJECTED, hospitalId));
//        } else {
//            // Trường hợp User là Staff nhưng chưa có bệnh viện
//            metrics.put("riskCases", 0L);
//            metrics.put("confirmedCases", 0L);
//            metrics.put("notEligibleCases", 0L);
//        }
//
//        return metrics;
//    }
//
//    @Override
//    public int[] getMonthlyTrendData() {
//        int[] monthlyTrend = new int[12];
//        List<Object[]> results = patientCaseRepository.countCasesByMonthInCurrentYear();
//        for (Object[] result : results) {
//            int month = ((Number) result[0]).intValue();
//            int count = ((Number) result[1]).intValue();
//            if (month >= 1 && month <= 12) monthlyTrend[month - 1] = count;
//        }
//        return monthlyTrend;
//    }
//
//    @Override
//    public Map<String, Long> getHospitalChartData() {
//        Map<String, Long> hospitalData = new LinkedHashMap<>();
//        List<Object[]> results = patientCaseRepository.countCasesByHospital();
//        for (Object[] result : results) {
//            String name = (String) result[0];
//            hospitalData.put(name != null ? name : "Chưa rõ BV", ((Number) result[1]).longValue());
//        }
//        return hospitalData;
//    }
//
//    @Override
//    public Map<String, Long> getStatusPieChartData() {
//        Map<String, Long> data = new HashMap<>();
//        List<Object[]> results = patientCaseRepository.countCasesByStatusGroup();
//        for (Object[] result : results) {
//            if (result[0] != null) data.put(((PatientStatus) result[0]).name(), ((Number) result[1]).longValue());
//        }
//        return data;
//    }
//}
package com.example.organdonationmanagement.service.impl;

import com.example.organdonationmanagement.entity.User;
import com.example.organdonationmanagement.entity.enums.DonorStatus;
import com.example.organdonationmanagement.entity.enums.PatientStatus;
import com.example.organdonationmanagement.repository.PatientCaseRepository;
import com.example.organdonationmanagement.repository.UserRepository;
import com.example.organdonationmanagement.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private PatientCaseRepository patientCaseRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ?
                ((UserDetails) principal).getUsername() : principal.toString();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Map<String, Long> getDashboardMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        User currentUser = getCurrentUser();
        Long hId = "ADMIN".equals(currentUser.getRole().name()) ? null : currentUser.getHospital().getId();

        long dongY = patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.FAMILY_ACCEPTED, hId);
        long dangCho = patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.PENDING, hId);

        System.out.println("DEBUG: User " + currentUser.getUsername() + " | HospitalID: " + hId);
        System.out.println("DEBUG: GD_DONG_Y count: " + dongY);
        System.out.println("DEBUG: DANG_CHO count: " + dangCho);

        metrics.put("CO_KHA_NANG", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.POTENTIAL, hId));
        metrics.put("GD_DONG_Y", dongY);
        metrics.put("GD_TU_CHOI", patientCaseRepository.countByDonorStatusAndHospitalId(DonorStatus.FAMILY_REJECTED, hId));
        metrics.put("DANG_CHO", dangCho);
        return metrics;
    }

    @Override
    public int[] getMonthlyTrendData() {
        int[] monthlyTrend = new int[12];
        List<Object[]> results = patientCaseRepository.countCasesByMonthInCurrentYear();
        for (Object[] result : results) {
            int month = ((Number) result[0]).intValue();
            int count = ((Number) result[1]).intValue();
            if (month >= 1 && month <= 12) monthlyTrend[month - 1] = count;
        }
        return monthlyTrend;
    }

    @Override
    public Map<String, Long> getHospitalChartData() {
        Map<String, Long> hospitalData = new LinkedHashMap<>();
        List<Object[]> results = patientCaseRepository.countCasesByHospital();
        for (Object[] result : results) {
            String name = (String) result[0];
            hospitalData.put(name != null ? name : "Chưa rõ BV", ((Number) result[1]).longValue());
        }
        return hospitalData;
    }

    @Override
    public Map<String, Long> getStatusPieChartData() {
        Map<String, Long> data = new HashMap<>();
        List<Object[]> results = patientCaseRepository.countCasesByStatusGroup();
        for (Object[] result : results) {
            if (result[0] != null) data.put(((PatientStatus) result[0]).name(), ((Number) result[1]).longValue());
        }
        return data;
    }
}

package com.example.organdonationmanagement.service.impl;

import com.example.organdonationmanagement.entity.enums.PatientStatus;
import com.example.organdonationmanagement.repository.PatientCaseRepository;
import com.example.organdonationmanagement.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private PatientCaseRepository patientCaseRepository;

    // 1. Lấy số liệu thật cho 4 thẻ Card đầu trang (Quét bảng patient_cases)
    @Override
    public Map<String, Long> getDashboardMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalCases", patientCaseRepository.count());
        metrics.put("riskCases", patientCaseRepository.countByStatus(PatientStatus.BRAIN_DEATH_1));
        metrics.put("confirmedCases", patientCaseRepository.countByStatus(PatientStatus.BRAIN_DEATH_2));
        metrics.put("notEligibleCases", patientCaseRepository.countByStatus(PatientStatus.BRAIN_DEATH_3));
        return metrics;
    }

    // 2. Lấy dữ liệu 12 tháng từ Database (Quét trường createdAt của ca bệnh)
    @Override
    public int[] getMonthlyTrendData() {
        int[] monthlyTrend = new int[12];
        List<Object[]> results = patientCaseRepository.countCasesByMonthInCurrentYear();
        for (Object[] result : results) {
            int month = ((Number) result[0]).intValue();
            int count = ((Number) result[1]).intValue();
            if (month >= 1 && month <= 12) {
                monthlyTrend[month - 1] = count;
            }
        }
        return monthlyTrend;
    }

    // 3. Thống kê số ca thực tế thuộc về từng Bệnh viện (Join bảng hospitals)
    @Override
    public Map<String, Long> getHospitalChartData() {
        Map<String, Long> hospitalData = new LinkedHashMap<>();
        List<Object[]> results = patientCaseRepository.countCasesByHospital();
        for (Object[] result : results) {
            String hospitalName = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            // Nếu ca bệnh chưa gán bệnh viện thì hiển thị "Chưa rõ BV"
            hospitalData.put(hospitalName != null ? hospitalName : "Chưa rõ BV", count);
        }
        return hospitalData;
    }

    // 4. Thống kê nhóm trạng thái thực tế phục vụ biểu đồ Tròn
    @Override
    public Map<String, Long> getStatusPieChartData() {
        Map<String, Long> statusData = new HashMap<>();
        List<Object[]> results = patientCaseRepository.countCasesByStatusGroup();
        for (Object[] result : results) {
            PatientStatus status = (PatientStatus) result[0];
            Long count = ((Number) result[1]).longValue();
            if (status != null) {
                statusData.put(status.name(), count);
            }
        }
        return statusData;
    }
}

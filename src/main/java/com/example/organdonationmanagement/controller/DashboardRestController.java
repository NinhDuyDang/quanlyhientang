package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.entity.PatientCase;
import com.example.organdonationmanagement.entity.enums.PatientStatus;
import com.example.organdonationmanagement.service.PatientCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DashboardRestController {

    private final PatientCaseService patientCaseService;

    @GetMapping("/api/dashboard/filter")
    public Map<String, Object> filterDashboard(
            @RequestParam(value = "hospitalId", required = false) Long hospitalId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<PatientCase> filteredCases = patientCaseService.search(hospitalId, status, fromDate, toDate);

        // 2. Tính toán lại số liệu cho 4 thẻ điểm dựa trên danh sách đã lọc
        long total = filteredCases.size();
        long risk = filteredCases.stream().filter(c -> c.getStatus() == PatientStatus.BRAIN_DEATH_1).count();
        long confirmed = filteredCases.stream().filter(c -> c.getStatus() == PatientStatus.BRAIN_DEATH_2).count();
        long notEligible = filteredCases.stream().filter(c -> c.getStatus() == PatientStatus.BRAIN_DEATH_3).count();

        // 3. Gom nhóm dữ liệu mới cho Biểu đồ Cột Bệnh viện dựa trên danh sách đã lọc
        Map<String, Long> hospitalChartData = filteredCases.stream()
                .filter(c -> c.getHospital() != null)
                .collect(Collectors.groupingBy(c -> c.getHospital().getName(), Collectors.counting()));

        // 4. Gom nhóm dữ liệu mới cho Biểu đồ Tròn Trạng thái dựa trên danh sách đã lọc
        Map<String, Long> statusChartData = filteredCases.stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting()));

        Map<String, Object> response = new HashMap<>();
        response.put("totalCases", total);
        response.put("riskCases", risk);
        response.put("confirmedCases", confirmed);
        response.put("notEligibleCases", notEligible);
        response.put("hospitalLabels", hospitalChartData.keySet());
        response.put("hospitalValues", hospitalChartData.values());
        response.put("statusMap", statusChartData);

        return response;
    }
}

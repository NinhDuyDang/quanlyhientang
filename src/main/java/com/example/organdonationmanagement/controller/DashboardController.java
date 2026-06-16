package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.repository.HospitalRepository;
import com.example.organdonationmanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping({"/", "/dashboard"})
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final HospitalRepository hospitalRepository;

    @GetMapping
    public String showDashboardPage(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("title", "Bảng điều khiển");

        // 1. Lấy metrics cho 4 thẻ Card
        Map<String, Long> metrics = dashboardService.getDashboardMetrics();
        model.addAttribute("totalCases", metrics.get("totalCases"));
        model.addAttribute("riskCases", metrics.get("riskCases"));
        model.addAttribute("confirmedCases", metrics.get("confirmedCases"));
        model.addAttribute("notEligibleCases", metrics.get("notEligibleCases"));

        // 2. Dữ liệu cho các biểu đồ
        model.addAttribute("lineChartData", dashboardService.getMonthlyTrendData());

        Map<String, Long> hospitalData = dashboardService.getHospitalChartData();
        model.addAttribute("hospitalLabels", hospitalData.keySet());
        model.addAttribute("hospitalValues", hospitalData.values());

        model.addAttribute("statusPieMap", dashboardService.getStatusPieChartData());

        // Danh sách bệnh viện cho bộ lọc
        model.addAttribute("hospitals", hospitalRepository.findAll());

        return "dashboard/index";
    }
}

package com.example.organdonationmanagement.service;

import java.util.Map;

public interface DashboardService {

    Map<String, Long> getDashboardMetrics();

    int[] getMonthlyTrendData();

    Map<String, Long> getHospitalChartData();

    Map<String, Long> getStatusPieChartData();
}

package com.example.organdonationmanagement.service;

import java.util.Map;

public interface DashboardService {

    // Lấy số liệu đếm tổng quan cho 4 Card (Tổng ca, Nguy cơ, Xác nhận, Không đủ)
    Map<String, Long> getDashboardMetrics();

    // Lấy mảng dữ liệu số ca theo 12 tháng (Dùng cho biểu đồ đường "Xu hướng")
    int[] getMonthlyTrendData();

    // Lấy dữ liệu dạng Key-Value (Tên bệnh viện -> Số ca) cho biểu đồ Cột
    Map<String, Long> getHospitalChartData();

    // Lấy dữ liệu dạng Key-Value (Trạng thái -> Số ca) cho biểu đồ Tròn
    Map<String, Long> getStatusPieChartData();
}

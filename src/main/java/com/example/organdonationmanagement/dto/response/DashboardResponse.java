package com.example.organdonationmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    private Long totalCases;

    private Map<String, Long>
            byHospital;

    private Map<String, Long>
            byStatus;

    private Map<String, Long>
            byMonth;
}

package com.example.organdonationmanagement.dto.request;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientCaseRequest {

    private String caseCode;

    private Long hospitalId;

    private Integer birthYear;

    private String patientName;

    private String gender;

    private String cause;

    private String status;

    private String donorStatus;

    private String note;

    private LocalDate incidentDate;
}

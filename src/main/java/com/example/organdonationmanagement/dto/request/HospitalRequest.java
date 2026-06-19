package com.example.organdonationmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRequest {
    private String code;
    private String name;
    private String province;
    private String address;
    private String phone;
    private String email;
}

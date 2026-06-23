package com.example.organdonationmanagement.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUpdateRequest {
    private String fullName;
    private Integer age;
    private String phoneNumber;
    private String workAddress;
    private MultipartFile avatarFile;
}

package com.example.organdonationmanagement.dto.request;

import lombok.Data; // Đảm bảo bạn đang dùng @Data của Lombok
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUpdateRequest {
    private String fullName;
    private Integer age;
    private String phoneNumber;
    private String workAddress;

    // THÊM DÒNG NÀY VÀO
    private MultipartFile avatarFile;
}

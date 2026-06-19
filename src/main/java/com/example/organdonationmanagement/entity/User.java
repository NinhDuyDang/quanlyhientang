package com.example.organdonationmanagement.entity;

import com.example.organdonationmanagement.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    private boolean enabled = true;

    @Enumerated(EnumType.STRING) // BẮT BUỘC ĐỂ LƯU CHỮ (VD: "ADMIN") THAY VÌ SỐ (VD: 0)
    @Column(name = "role", length = 20)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;


    // --- CÁC TRƯỜNG MỚI CẦN THÊM ĐỂ LƯU THÔNG TIN CÁ NHÂN ---
    private String fullName;      // Họ và tên
    private Integer age;          // Tuổi
    private String phoneNumber;   // Số điện thoại liên hệ
    private String workAddress;   // Địa chỉ làm việc
    private String avatarUrl;     // Đường dẫn ảnh đại diện (nếu bạn muốn lưu đường dẫn ảnh)
    // -------------------------------------------------------

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

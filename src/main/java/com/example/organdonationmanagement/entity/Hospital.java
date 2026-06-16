package com.example.organdonationmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "hospitals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String code;

        @Column(nullable = false)
        private String name;

        private String address;

        private String province;

        private String phone;

        private String email;

        private LocalDateTime createdAt;

        @PrePersist
        public void prePersist() {
            createdAt = LocalDateTime.now();
        }
}

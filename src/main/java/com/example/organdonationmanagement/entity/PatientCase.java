package com.example.organdonationmanagement.entity;

import com.example.organdonationmanagement.entity.enums.Cause;
import com.example.organdonationmanagement.entity.enums.DonorStatus;
import com.example.organdonationmanagement.entity.enums.Gender;
import com.example.organdonationmanagement.entity.enums.PatientStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String caseCode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Cause cause;

    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    @Enumerated(EnumType.STRING)
    private DonorStatus donorStatus;
    @Column(length = 50)
    private String bloodtype;
    @Column(length = 1000)
    private String note;
    @Column(name = "patient_name", nullable = false)
    private String patientName;
    private LocalDate incidentDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

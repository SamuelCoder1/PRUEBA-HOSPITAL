package com.riwi.Hospital.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne
    private Patient patient;

    private LocalDateTime appointmentDate;

    private String doctorName;

    private String doctorPhone;

    private String patientName;

    private String diagnosis;

    private String appointmentReason;
}

package com.riwi.Hospital.domain.entities;

import com.riwi.Hospital.domain.enums.MedicStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "doctors")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    private String speciality;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private MedicStatus status;
}

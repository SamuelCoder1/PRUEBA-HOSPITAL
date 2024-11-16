package com.riwi.Hospital.application.dtos.requests;

import com.riwi.Hospital.domain.entities.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientWithoutId {

    @NotNull(message = "User is required")
    private User user;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;
}

package com.riwi.Hospital.application.dtos.requests;

import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.enums.MedicStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorWithoutId {

    @NotNull(message = "User is required")
    private User user;

    @NotNull(message = "Status is required")
    private MedicStatus status;

    private String speciality;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}

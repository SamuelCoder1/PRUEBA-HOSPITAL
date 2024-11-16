package com.riwi.Hospital.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithoutId {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Document is required")
    private String document;

    @NotBlank(message = "Password is required")
    private String password;
}

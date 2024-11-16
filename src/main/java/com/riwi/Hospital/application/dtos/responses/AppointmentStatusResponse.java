package com.riwi.Hospital.application.dtos.responses;

import com.riwi.Hospital.domain.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentStatusResponse {

    private AppointmentStatus status;

}

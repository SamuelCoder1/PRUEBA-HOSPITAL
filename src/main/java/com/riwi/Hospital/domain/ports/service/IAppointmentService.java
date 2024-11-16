package com.riwi.Hospital.domain.ports.service;

import com.riwi.Hospital.application.dtos.requests.AppointmentWithoutId;
import com.riwi.Hospital.application.services.generic.*;
import com.riwi.Hospital.domain.entities.Appointment;

public interface IAppointmentService extends
        Create<Appointment, AppointmentWithoutId>,
        ReadById<Appointment, Long>,
        ReadAll<Appointment>,
        Update<Long, Appointment, AppointmentWithoutId>,
        Delete<Long> {
}

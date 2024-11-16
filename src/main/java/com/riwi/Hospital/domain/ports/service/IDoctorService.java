package com.riwi.Hospital.domain.ports.service;

import com.riwi.Hospital.application.dtos.requests.DoctorWithoutId;
import com.riwi.Hospital.application.services.generic.*;
import com.riwi.Hospital.domain.entities.Doctor;

public interface IDoctorService extends
        Create<Doctor, DoctorWithoutId>,
        ReadById<Doctor, Long>,
        ReadAll<Doctor>,
        Update<Long, Doctor, DoctorWithoutId>,
        Delete<Long> {
}

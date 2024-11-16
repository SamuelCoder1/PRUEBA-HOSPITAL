package com.riwi.Hospital.domain.ports.service;

import com.riwi.Hospital.application.dtos.requests.PatientWithoutId;
import com.riwi.Hospital.application.services.generic.*;
import com.riwi.Hospital.domain.entities.Patient;

public interface IPatientService extends
        Create<Patient, PatientWithoutId>,
        ReadByDocument<Patient, String>,
        ReadAll<Patient>,
        UpdateByDocument<String, Patient, PatientWithoutId>,
        DeleteByDocument<String> {
}

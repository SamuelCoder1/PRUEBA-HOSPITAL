package com.riwi.Hospital.infrastructure.persistence;

import com.riwi.Hospital.domain.entities.Appointment;
import com.riwi.Hospital.domain.entities.MedicalHistory;
import com.riwi.Hospital.domain.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatient(Patient patient);
}

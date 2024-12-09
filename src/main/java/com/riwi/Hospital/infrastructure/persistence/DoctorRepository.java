package com.riwi.Hospital.infrastructure.persistence;

import com.riwi.Hospital.domain.entities.Appointment;
import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.enums.MedicStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByUserDocument(String document);;
    List<Doctor> findBySpeciality(String speciality);
    List<Doctor> findByStatus(MedicStatus status);
}

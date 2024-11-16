package com.riwi.Hospital.infrastructure.persistence;

import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser_Document(String document);

    Optional<Patient> findByUser(User user);
}

package com.riwi.Hospital.infrastructure.persistence;

import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByUserDocument(String document);;
    List<Doctor> findBySpeciality(String speciality);
}

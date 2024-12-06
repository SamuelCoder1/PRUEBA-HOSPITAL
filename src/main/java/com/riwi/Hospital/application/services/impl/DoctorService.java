package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.exception.UnauthorizedAccessException;
import com.riwi.Hospital.application.dtos.requests.DoctorWithoutId;
import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.enums.Role;
import com.riwi.Hospital.domain.ports.service.IDoctorService;
import com.riwi.Hospital.infrastructure.persistence.DoctorRepository;
import com.riwi.Hospital.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService implements IDoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public Doctor create(DoctorWithoutId doctorDTO) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only admins can create doctors.");
        }

        User user = new User();
        user.setDocument(doctorDTO.getUser().getDocument());
        user.setName(doctorDTO.getUser().getName());
        user.setPassword(passwordEncoder.encode(doctorDTO.getUser().getPassword()));
        user.setRole(Role.DOCTOR);

        User savedUser = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(savedUser)
                .status(doctorDTO.getStatus())
                .speciality(doctorDTO.getSpeciality())
                .phoneNumber(doctorDTO.getPhoneNumber())
                .build();

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(Long id, DoctorWithoutId doctorDTO) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only admins can update doctors.");
        }

        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundExceptions("Doctor not found with ID: " + id));

        User user = existingDoctor.getUser();
        user.setDocument(doctorDTO.getUser().getDocument());
        user.setName(doctorDTO.getUser().getName());
        user.setPassword(passwordEncoder.encode(doctorDTO.getUser().getPassword()));
        user.setRole(Role.DOCTOR);

        userRepository.save(user);

        existingDoctor.setStatus(doctorDTO.getStatus());
        existingDoctor.setSpeciality(doctorDTO.getSpeciality());
        existingDoctor.setPhoneNumber(doctorDTO.getPhoneNumber());

        return doctorRepository.save(existingDoctor);
    }

    @Override
    public void delete(Long id) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only admins can delete doctors.");
        }

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundExceptions("Doctor not found with ID: " + id));

        doctorRepository.delete(doctor);
    }

    @Override
    public List<Doctor> readAll() {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only admins can read all doctors.");
        }

        if (doctorRepository.findAll().isEmpty()) {
            throw new GenericNotFoundExceptions("No doctors registered");
        }

        return doctorRepository.findAll();
    }

    @Override
    public Optional<Doctor> readById(Long id) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only admins and doctors can view doctor details.");
        }

        return doctorRepository.findById(id);
    }

    public List<Doctor> findBySpeciality(String speciality) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only admins can search doctors by speciality.");
        }

        List<Doctor> doctors = doctorRepository.findBySpeciality(speciality);
        if (doctors.isEmpty()) {
            throw new GenericNotFoundExceptions("No doctors found with the speciality: " + speciality);
        }

        return doctors;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ADMIN.name()));
    }
}

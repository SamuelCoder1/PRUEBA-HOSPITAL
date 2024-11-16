package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.exception.UnauthorizedAccessException;
import com.riwi.Hospital.application.dtos.requests.PatientWithoutId;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.enums.Role;
import com.riwi.Hospital.domain.ports.service.IPatientService;
import com.riwi.Hospital.infrastructure.persistence.PatientRepository;
import com.riwi.Hospital.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService implements IPatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Patient create(PatientWithoutId patientDTO) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only an admin can create patients.");
        }

        // Crear el usuario completo
        User user = new User();
        user.setDocument(patientDTO.getUser().getDocument());
        user.setName(patientDTO.getUser().getName());
        user.setPassword(patientDTO.getUser().getPassword());
        user.setRole(Role.PATIENT);

        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .phoneNumber(patientDTO.getPhoneNumber())
                .address(patientDTO.getAddress())
                .dateOfBirth(patientDTO.getDateOfBirth())
                .build();

        return patientRepository.save(patient);
    }

    @Override
    public void deleteByDocument(String document) {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Access denied: Only an admin can delete patients.");
        }

        User user = userRepository.findByDocument(document)
                .orElseThrow(() -> new GenericNotFoundExceptions("User not found with document: " + document));

        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new GenericNotFoundExceptions("Patient not found for user with document: " + document));

        patientRepository.delete(patient);
    }

    @Override
    public List<Patient> readAll() {
        if (!isAdmin() && !isDoctor()) {
            throw new UnauthorizedAccessException("Access denied: Only admins and doctors can read all patients.");
        }

        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> readByDocument(String document) {
        if (!isAdmin() && !isDoctor()) {
            throw new UnauthorizedAccessException("Access denied: Only admins and doctors can read patients by ID.");
        }

        return patientRepository.findByUser_Document(document);
    }

    @Override
    public Patient updateByDocument(String document, PatientWithoutId patientDTO) {
        if (!isAdmin() && !isDoctor()) {
            throw new UnauthorizedAccessException("Access denied: Only admins and doctors can update patients.");
        }

        User user = userRepository.findByDocument(document)
                .orElseThrow(() -> new GenericNotFoundExceptions("User not found with document: " + document));

        Patient existingPatient = patientRepository.findByUser(user)
                .orElseThrow(() -> new GenericNotFoundExceptions("Patient not found for user with document: " + document));

        existingPatient.setPhoneNumber(patientDTO.getPhoneNumber());
        existingPatient.setAddress(patientDTO.getAddress());
        existingPatient.setDateOfBirth(patientDTO.getDateOfBirth());

        return patientRepository.save(existingPatient);
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ADMIN.name()));
    }

    private boolean isDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.DOCTOR.name()));
    }
}

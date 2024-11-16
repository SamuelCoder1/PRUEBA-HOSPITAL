package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.requests.UserWithoutId;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.entities.User;
import com.riwi.Hospital.domain.enums.Role;
import com.riwi.Hospital.domain.ports.service.IUserService;
import com.riwi.Hospital.infrastructure.persistence.DoctorRepository;
import com.riwi.Hospital.infrastructure.persistence.PatientRepository;
import com.riwi.Hospital.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(UserWithoutId userDTO) {
        // Crear el nuevo usuario
        User user = new User();
        user.setDocument(userDTO.getDocument());
        user.setPassword(encryptPassword(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setRole(Role.PATIENT);

        user = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patientRepository.save(patient);


        System.out.println("Usuario guardado: " + user);

        return user;
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password); // Encripta la contraseña
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ADMIN.name()));
    }


    @Override
    public Optional<User> readByDocument(String document) {
        Optional<User> user = userRepository.findByDocument(document);
        if (user.isEmpty()) {
            throw new GenericNotFoundExceptions("User not found with document: " + document);
        }
        return user;
    }
}

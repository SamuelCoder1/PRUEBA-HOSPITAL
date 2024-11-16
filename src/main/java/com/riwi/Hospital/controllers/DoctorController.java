package com.riwi.Hospital.controllers;

import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.requests.DoctorWithoutId;
import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.ports.service.IDoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doctors")
@Tag(name = "Doctors", description = "API for managing doctors")
@CrossOrigin("*")
public class DoctorController {

    @Autowired
    private IDoctorService doctorService;

    @Operation(summary = "Create a new doctor", description = "Allows an admin to create a new doctor.")
    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Doctor> createDoctor(
            @Parameter(description = "Details of the doctor to create", required = true)
            @RequestBody DoctorWithoutId doctorDTO) {
        Doctor createdDoctor = doctorService.create(doctorDTO);
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a doctor", description = "Allows an admin to update the details of an existing doctor.")
    @PutMapping("/update/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Doctor> updateDoctor(
            @Parameter(description = "ID of the doctor to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated details of the doctor", required = true)
            @RequestBody DoctorWithoutId doctorDTO) {
        Doctor updatedDoctor = doctorService.update(id, doctorDTO);
        return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
    }


    @Operation(summary = "Delete a doctor", description = "Allows an admin to delete a doctor by their ID.")
    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> deleteDoctor(
            @Parameter(description = "ID of the doctor to delete", required = true)
            @PathVariable Long id) {
        doctorService.delete(id);
        return new ResponseEntity<>("Doctor deleted successfully.", HttpStatus.NO_CONTENT);
    }


    @Operation(summary = "Get all doctors", description = "Allows an admin to retrieve a list of all doctors.")
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.readAll();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @Operation(summary = "Get a doctor by ID", description = "Allows an admin to retrieve a doctor's details by their ID.")
    @GetMapping("/readById/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Doctor> getDoctorById(
            @Parameter(description = "ID of the doctor to retrieve", required = true)
            @PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.readById(id);
        return doctor.map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundExceptions("Doctor not found with ID: " + id));
    }
}


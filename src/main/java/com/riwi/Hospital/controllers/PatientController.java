package com.riwi.Hospital.controllers;

import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.requests.PatientWithoutId;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.ports.service.IPatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "API for managing patients")
@CrossOrigin(origins = {"http://127.0.0.1:8000", "http://localhost:8000"})
public class PatientController {

    @Autowired
    private IPatientService patientService;

    @Operation(summary = "Create a new patient", description = "Allows an admin to create a new patient.")
    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Patient> createPatient(
            @Parameter(description = "Details of the patient to create", required = true)
            @RequestBody PatientWithoutId patientDTO) {
        Patient createdPatient = patientService.create(patientDTO);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a patient by document", description = "Allows an admin to delete a patient using their document.")
    @DeleteMapping("/delete/{document}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> deletePatientByDocument(
            @Parameter(description = "Document of the patient to delete", required = true)
            @PathVariable String document) {
        patientService.deleteByDocument(document);
        return new ResponseEntity<>("Patient deleted successfully.", HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get all patients", description = "Allows admins and doctors to retrieve a list of all patients.")
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.readAll();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @Operation(
            summary = "Get a patient by Document",
            description = "Allows admins and doctors to retrieve a patient's details by their document."
    )
    @GetMapping("/readByDocument/{document}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Patient> getPatientByDocument(
            @Parameter(description = "Document of the patient to retrieve", required = true)
            @PathVariable String document) {

        Patient patient = patientService.readByDocument(document)
                .orElseThrow(() -> new GenericNotFoundExceptions("Patient not found with document: " + document));

        return ResponseEntity.ok(patient);
    }

    @Operation(summary = "Update a patient by document", description = "Allows admins and doctors to update a patient's details using their document.")
    @PutMapping("/update/{document}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Patient> updatePatientByDocument(
            @Parameter(description = "Document of the patient to update", required = true)
            @PathVariable String document,
            @Parameter(description = "Updated details of the patient", required = true)
            @RequestBody PatientWithoutId patientDTO) {
        Patient updatedPatient = patientService.updateByDocument(document, patientDTO);
        return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
    }
}

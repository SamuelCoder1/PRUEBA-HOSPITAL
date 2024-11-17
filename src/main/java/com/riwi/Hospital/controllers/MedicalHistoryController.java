package com.riwi.Hospital.controllers;

import com.riwi.Hospital.application.services.impl.MedicalHistoryService;
import com.riwi.Hospital.domain.entities.MedicalHistory;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/medical-history")
@CrossOrigin("http://127.0.0.1:5500")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    // Endpoint para generar historial médico en formato PDF
    @GetMapping("/patient/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> generateMedicalHistoryPdf(@Parameter(description = "ID of the patient")@PathVariable Long id) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = medicalHistoryService.generateMedicalHistoryPdf(id);
            byte[] pdfContent = byteArrayOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=historial_medico.pdf");
            headers.add("Content-Type", "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generando el PDF: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addMedicalHistory(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam String diagnosis,
            @RequestParam String treatment,
            @RequestParam String prescriptions) {
        try {
            // Llamar al servicio para agregar el historial médico
            MedicalHistory medicalHistory = medicalHistoryService.addMedicalHistory(patientId, doctorId, diagnosis, treatment, prescriptions);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Medical history added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agregar historial médico: " + e.getMessage());
        }
    }
}


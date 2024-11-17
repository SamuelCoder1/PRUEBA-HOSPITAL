package com.riwi.Hospital.controllers;

import com.riwi.Hospital.application.dtos.responses.AppointmentStatusResponse;
import com.riwi.Hospital.application.dtos.requests.AppointmentWithoutId;
import com.riwi.Hospital.application.services.impl.AppointmentService;
import com.riwi.Hospital.domain.entities.Appointment;
import com.riwi.Hospital.domain.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@CrossOrigin("http://127.0.0.1:5500")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Operation(summary = "Get all appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments")
    })
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public List<Appointment> getAllAppointments() {
        return appointmentService.readAll();
    }

    @Operation(summary = "Get an appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("/readById/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Appointment getAppointmentById(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        return appointmentService.readById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    @Operation(summary = "Create a new appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created appointment"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/create")
    @SecurityRequirement(name = "bearerAuth")
    public Appointment createAppointment(@RequestBody AppointmentWithoutId appointmentWithoutId) {
        return appointmentService.create(appointmentWithoutId);
    }

    @Operation(summary = "Update an existing appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PutMapping("/update/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Appointment updateAppointment(
            @PathVariable Long id, @RequestBody AppointmentWithoutId appointmentWithoutId) {
        return appointmentService.update(id, appointmentWithoutId);
    }

    @Operation(summary = "Delete an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.delete(id);
    }

    @Operation(summary = "Get appointments by doctor ID and date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/readAppointmentsByDoctor/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Appointment> getAppointmentsByDoctor(
            @Parameter(description = "Doctor ID") @PathVariable Long id,
            @Parameter(description = "Appointment date") @RequestParam(required = false) LocalDateTime appointmentDate) {
        return appointmentService.getAppointmentsByDoctor(id, appointmentDate);
    }

    @Operation(summary = "Update the status of an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated status"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PutMapping("/update/status/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public AppointmentStatusResponse updateAppointmentStatus(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Parameter(description = "New appointment status") @RequestParam AppointmentStatus status) {
        Appointment updatedAppointment = appointmentService.updateStatus(id, status);
        return new AppointmentStatusResponse(updatedAppointment.getStatus());
    }

    @Operation(summary = "Get all available appointments for a patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/getAppointmentsByPatient/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Appointment> getAppointmentsByPatient(@PathVariable Long id) {
        return appointmentService.getAppointmentsByPatient(id);
    }

    @Operation(summary = "Get available time slots for a doctor")
    @GetMapping("/available-slots/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public List<String> getAvailableTimeSlots(
            @Parameter(description = "Doctor ID") @PathVariable Long id,
            @Parameter(description = "Date in yyyy-MM-dd format") @RequestParam String date) {

        // Convertir el String 'date' a LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Llamar al servicio para obtener los horarios disponibles
        return appointmentService.getAvailableTimeSlots(id, localDate);
    }
}

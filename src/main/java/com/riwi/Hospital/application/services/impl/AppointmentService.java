package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.application.dtos.requests.AppointmentWithoutId;
import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.exception.UnauthorizedAccessException;
import com.riwi.Hospital.domain.entities.Appointment;
import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.enums.AppointmentStatus;
import com.riwi.Hospital.domain.enums.MedicStatus;
import com.riwi.Hospital.domain.ports.service.IAppointmentService;
import com.riwi.Hospital.infrastructure.persistence.AppointmentRepository;
import com.riwi.Hospital.infrastructure.persistence.DoctorRepository;
import com.riwi.Hospital.infrastructure.persistence.PatientRepository;
import com.riwi.Hospital.application.services.impl.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class AppointmentService implements IAppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public Appointment create(AppointmentWithoutId appointmentWithoutId) {
        Doctor doctor = doctorRepository.findById(appointmentWithoutId.getDoctorId())
                .orElseThrow(() -> new GenericNotFoundExceptions("Doctor not found with ID: " + appointmentWithoutId.getDoctorId()));

        LocalDateTime appointmentDateTime = appointmentWithoutId.getAppointmentDate();
        LocalDateTime endOfAppointment = appointmentDateTime.plusMinutes(30);

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                appointmentWithoutId.getDoctorId(), appointmentDateTime, endOfAppointment);

        if (!existingAppointments.isEmpty()) {
            throw new UnauthorizedAccessException("Doctor already has an appointment during this time.");
        }

        Patient patient = patientRepository.findById(appointmentWithoutId.getPatientId())
                .orElseThrow(() -> new GenericNotFoundExceptions("Patient not found with ID: " + appointmentWithoutId.getPatientId()));

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(appointmentDateTime)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        doctor.setStatus(MedicStatus.UNAVAILABLE);
        doctorRepository.save(doctor);

        notificationService.sendNotification("appointments", "new_appointment",
                "New appointment scheduled for the patient " + patient.getUser().getName());

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(Long id, AppointmentWithoutId appointmentWithoutId) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundExceptions("Appointment not found with ID: " + id));

        LocalDateTime newAppointmentDateTime = appointmentWithoutId.getAppointmentDate();
        LocalDateTime endOfAppointment = newAppointmentDateTime.plusMinutes(30);

        List<Appointment> overlappingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                existingAppointment.getDoctor().getId(), newAppointmentDateTime, endOfAppointment);

        if (!overlappingAppointments.isEmpty()) {
            throw new UnauthorizedAccessException("Doctor already has an appointment during this time.");
        }

        existingAppointment.setAppointmentDate(newAppointmentDateTime);

        if (LocalDateTime.now().isBefore(newAppointmentDateTime)) {
            existingAppointment.setStatus(AppointmentStatus.CONFIRMED);
        } else {
            existingAppointment.setStatus(AppointmentStatus.FINISHED);
        }

        Doctor doctor = existingAppointment.getDoctor();
        if (overlappingAppointments.isEmpty()) {
            doctor.setStatus(MedicStatus.AVAILABLE);
        } else {
            doctor.setStatus(MedicStatus.UNAVAILABLE);
        }
        doctorRepository.save(doctor);

        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public List<Appointment> readAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> readById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundExceptions("Appointment not found with ID: " + id));

        Doctor doctor = appointment.getDoctor();
        doctor.setStatus(MedicStatus.AVAILABLE);
        doctorRepository.save(doctor);

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        String message = "The appointment of " + appointment.getPatient().getUser().getName() + " has been cancelled.";
        notificationService.sendNotification("appointments", "appointment_cancelled", message);

        appointmentRepository.delete(appointment);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId, LocalDate appointmentDate) {
        if (appointmentDate != null) {
            LocalDateTime startOfDay = appointmentDate.atStartOfDay();

            LocalDateTime endOfDay = appointmentDate.atTime(23, 59, 59, 999999);

            return appointmentRepository.findByDoctorIdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay);
        } else {
            return appointmentRepository.findByDoctorId(doctorId);
        }
    }

    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, AppointmentStatus.PENDING);
    }

    public List<String> getAvailableTimeSlots(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(18, 0);

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                doctorId, startOfDay, endOfDay);

        List<String> allSlots = generateAllSlots(startOfDay, endOfDay);

        List<String> availableSlots = new ArrayList<>(allSlots);

        for (Appointment appointment : existingAppointments) {
            LocalDateTime appointmentTime = appointment.getAppointmentDate();
            String occupiedSlot = appointmentTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            LocalDateTime endOfAppointment = appointmentTime.plusMinutes(30);
            String endSlot = endOfAppointment.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            availableSlots.remove(occupiedSlot);

            LocalDateTime currentSlot = appointmentTime.plusMinutes(30);
            while (currentSlot.isBefore(endOfAppointment)) {
                String nextSlot = currentSlot.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                availableSlots.remove(nextSlot);
                currentSlot = currentSlot.plusMinutes(30);
            }
        }

        return availableSlots;
    }

    private List<String> generateAllSlots(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        List<String> slots = new ArrayList<>();
        LocalDateTime currentSlot = startOfDay;

        while (currentSlot.isBefore(endOfDay)) {
            String slot = currentSlot.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            slots.add(slot);
            currentSlot = currentSlot.plusMinutes(30);
        }

        return slots;
    }
}

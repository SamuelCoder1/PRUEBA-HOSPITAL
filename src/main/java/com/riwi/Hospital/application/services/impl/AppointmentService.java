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

        if (doctor.getStatus() == MedicStatus.UNAVAILABLE) {
            throw new UnauthorizedAccessException("Doctor is unavailable for appointments.");
        }

        LocalDateTime appointmentDateTime = appointmentWithoutId.getAppointmentDate();

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(
                appointmentWithoutId.getDoctorId(), appointmentDateTime);

        if (!existingAppointments.isEmpty()) {
            throw new UnauthorizedAccessException("Doctor already has an appointment at this time.");
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

        String message = "New appointment scheduled for the patient " + patient.getUser().getName();
        notificationService.sendNotification("appointments", "new_appointment", message);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(Long id, AppointmentWithoutId appointmentWithoutId) {

        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundExceptions("Appointment not found with ID: " + id));

        LocalDateTime newAppointmentDateTime = appointmentWithoutId.getAppointmentDate();
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(
                existingAppointment.getDoctor().getId(), newAppointmentDateTime);

        if (!existingAppointments.isEmpty()) {
            throw new UnauthorizedAccessException("Doctor already has an appointment at this time.");
        }

        existingAppointment.setAppointmentDate(newAppointmentDateTime);

        LocalDateTime now = LocalDateTime.now();
        if (now.isEqual(newAppointmentDateTime)) {
            existingAppointment.setStatus(AppointmentStatus.CONFIRMED);
        } else {
            existingAppointment.setStatus(AppointmentStatus.FINISHED);
            Doctor doctor = existingAppointment.getDoctor();
            doctor.setStatus(MedicStatus.AVAILABLE);
            doctorRepository.save(doctor);
        }

        String message = "The appointment of " + existingAppointment.getPatient().getUser().getName() + " has been updated.";
        notificationService.sendNotification("appointments", "appointment_updated", message);

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

    public List<Appointment> getAppointmentsByDoctor(Long doctorId, LocalDateTime appointmentDate) {
        if (appointmentDate != null) {
            return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, appointmentDate);
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
            availableSlots.remove(occupiedSlot);
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

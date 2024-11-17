package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.application.dtos.requests.AppointmentWithoutId;
import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.application.dtos.exception.UnauthorizedAccessException;
import com.riwi.Hospital.domain.entities.Appointment;
import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.entities.MedicalHistory;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.enums.AppointmentStatus;
import com.riwi.Hospital.domain.enums.MedicStatus;
import com.riwi.Hospital.domain.ports.service.IAppointmentService;
import com.riwi.Hospital.infrastructure.persistence.AppointmentRepository;
import com.riwi.Hospital.infrastructure.persistence.DoctorRepository;
import com.riwi.Hospital.infrastructure.persistence.MedicalHistoryRepository;
import com.riwi.Hospital.infrastructure.persistence.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService implements IAppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Override
    @Transactional
    public Appointment create(AppointmentWithoutId appointmentWithoutId) {

        // Buscar doctor por ID
        Doctor doctor = doctorRepository.findById(appointmentWithoutId.getDoctorId())
                .orElseThrow(() -> new GenericNotFoundExceptions("Doctor not found with ID: " + appointmentWithoutId.getDoctorId()));

        // Verificar que el doctor esté disponible
        if (doctor.getStatus() == MedicStatus.UNAVAILABLE) {
            throw new UnauthorizedAccessException("Doctor is unavailable for appointments.");
        }

        // Convertir la fecha de la cita
        LocalDateTime appointmentDateTime = appointmentWithoutId.getAppointmentDate();

        // Verificar que el doctor no tenga otra cita a la misma hora
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(
                appointmentWithoutId.getDoctorId(), appointmentDateTime);

        if (!existingAppointments.isEmpty()) {
            throw new UnauthorizedAccessException("Doctor already has an appointment at this time.");
        }

        // Buscar paciente por ID
        Patient patient = patientRepository.findById(appointmentWithoutId.getPatientId())
                .orElseThrow(() -> new GenericNotFoundExceptions("Patient not found with ID: " + appointmentWithoutId.getPatientId()));

        // Crear la cita (solo guardando la fecha, doctor y paciente)
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(appointmentDateTime)  // Guardar solo la fecha y la hora
                .status(AppointmentStatus.PENDING)  // Establecer estado pendiente
                .build();

        // Cambiar el estado del doctor a UNAVAILABLE
        doctor.setStatus(MedicStatus.UNAVAILABLE);
        doctorRepository.save(doctor);

        // Guardar la cita en la base de datos
        return appointmentRepository.save(appointment);  // Guardar solo los datos requeridos
    }


    @Override
    public Appointment update(Long id, AppointmentWithoutId appointmentWithoutId) {

        // Buscar la cita existente
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new GenericNotFoundExceptions("Appointment not found with ID: " + id));

        // Verificar que el doctor no tenga otra cita a la misma hora
        LocalDateTime newAppointmentDateTime = appointmentWithoutId.getAppointmentDate();
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(
                existingAppointment.getDoctor().getId(), newAppointmentDateTime);

        if (!existingAppointments.isEmpty()) {
            throw new UnauthorizedAccessException("Doctor already has an appointment at this time.");
        }

        // Establecer la nueva fecha de la cita
        existingAppointment.setAppointmentDate(newAppointmentDateTime);

        // Asegurarse de que se compara con la fecha actual
        LocalDateTime now = LocalDateTime.now();
        if (now.isEqual(newAppointmentDateTime)) {
            existingAppointment.setStatus(AppointmentStatus.IN_PROGRESS);
        } else {
            existingAppointment.setStatus(AppointmentStatus.FINISHED);
            Doctor doctor = existingAppointment.getDoctor();
            doctor.setStatus(MedicStatus.AVAILABLE);
            doctorRepository.save(doctor);
        }

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
        // Convertir LocalDate a LocalDateTime para el inicio y fin del día
        LocalDateTime startOfDay = date.atStartOfDay();  // Obtiene 00:00 horas de ese día
        LocalDateTime endOfDay = date.atTime(18, 0);   // Fin del día a las 06:00 PM (18:00)

        // Obtener todas las citas existentes para ese doctor entre el rango de fecha
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                doctorId, startOfDay, endOfDay);

        // Lista de todos los intervalos de 30 minutos entre el inicio y fin del día
        List<String> allSlots = generateAllSlots(startOfDay, endOfDay);

        // Lista que almacenará los horarios disponibles
        List<String> availableSlots = new ArrayList<>(allSlots);

        // Eliminar de los horarios disponibles los que ya están ocupados por una cita
        for (Appointment appointment : existingAppointments) {
            LocalDateTime appointmentTime = appointment.getAppointmentDate();
            String occupiedSlot = appointmentTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            availableSlots.remove(occupiedSlot);  // Eliminar slot ocupado
        }

        return availableSlots;
    }

    /**
     * Genera una lista de todos los intervalos de 30 minutos entre el inicio y fin del día.
     *
     * @param startOfDay Hora de inicio del día como LocalDateTime.
     * @param endOfDay Hora de fin del día como LocalDateTime.
     * @return Lista de todos los intervalos disponibles en formato "HH:mm".
     */
    private List<String> generateAllSlots(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        List<String> slots = new ArrayList<>();
        LocalDateTime currentSlot = startOfDay;

        // Crear intervalos de 30 minutos entre el inicio y fin del día
        while (currentSlot.isBefore(endOfDay)) {
            String slot = currentSlot.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            slots.add(slot);
            currentSlot = currentSlot.plusMinutes(30);  // Avanzar 30 minutos
        }

        return slots;
    }
}

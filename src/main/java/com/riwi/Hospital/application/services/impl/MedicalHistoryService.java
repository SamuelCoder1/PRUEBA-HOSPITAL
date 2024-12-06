package com.riwi.Hospital.application.services.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.riwi.Hospital.domain.entities.Appointment;
import com.riwi.Hospital.domain.entities.MedicalHistory;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.enums.AppointmentStatus;
import com.riwi.Hospital.infrastructure.persistence.AppointmentRepository;
import com.riwi.Hospital.infrastructure.persistence.MedicalHistoryRepository;
import com.riwi.Hospital.infrastructure.persistence.PatientRepository;
import com.riwi.Hospital.infrastructure.persistence.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public MedicalHistory addMedicalHistory(Long patientId, Long doctorId, String diagnosis, String reasonDate) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        Appointment appointment = appointmentRepository.findByPatientAndDoctorAndStatus(patient, doctor, AppointmentStatus.CONFIRMED)
                .orElseThrow(() -> new IllegalArgumentException("No confirmed appointment found for this patient and doctor."));

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setPatient(patient);
        medicalHistory.setDoctor(doctor);
        medicalHistory.setDiagnosis(diagnosis);
        medicalHistory.setAppointmentReason(reasonDate);
        medicalHistory.setAppointmentDate(appointment.getAppointmentDate());
        medicalHistory.setDoctorName(doctor.getUser().getName());
        medicalHistory.setDoctorPhone(doctor.getPhoneNumber());
        medicalHistory.setPatientName(patient.getUser().getName());

        return medicalHistoryRepository.save(medicalHistory);
    }

    public ByteArrayOutputStream generateMedicalHistoryPdf(Long patientId) throws DocumentException, IOException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByPatient(patient);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        document.add(new Paragraph("Medical History of: " + patient.getUser().getName()));
        document.add(new Paragraph("Patient Document: " + patient.getUser().getDocument()));
        document.add(new Paragraph("\n"));

        if (medicalHistories.isEmpty()) {
            document.add(new Paragraph("No medical history available for this patient."));
        } else {
            for (MedicalHistory history : medicalHistories) {
                Doctor doctor = history.getDoctor();
                document.add(new Paragraph("Doctor: " + doctor.getUser().getName()));
                document.add(new Paragraph("Doctor Phone: " + doctor.getPhoneNumber()));
                document.add(new Paragraph("Date of appointment: " + history.getAppointmentDate()));
                document.add(new Paragraph("Diagnosis: " + history.getDiagnosis()));
                document.add(new Paragraph("Reason of Date: " + history.getAppointmentReason()));
                document.add(new Paragraph("\n"));
            }
        }
        document.close();
        return byteArrayOutputStream;
    }
}

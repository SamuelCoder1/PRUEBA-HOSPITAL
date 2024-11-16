package com.riwi.Hospital.application.services.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.riwi.Hospital.application.dtos.exception.GenericNotFoundExceptions;
import com.riwi.Hospital.domain.entities.MedicalHistory;
import com.riwi.Hospital.domain.entities.Patient;
import com.riwi.Hospital.domain.entities.Doctor;
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

    public MedicalHistory addMedicalHistory(Long patientId, Long doctorId, String diagnosis, String treatment, String prescriptions) {
        // Obtener el paciente por ID
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        // Obtener el doctor por ID
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        // Crear un nuevo historial médico
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setPatient(patient);
        medicalHistory.setDoctor(doctor);
        medicalHistory.setDiagnosis(diagnosis);
        medicalHistory.setTreatment(treatment);
        medicalHistory.setPrescriptions(prescriptions);
        medicalHistory.setConsultationDate(LocalDateTime.now());  // Usamos la fecha actual para la cita médica

        // Guardar el historial médico en la base de datos
        return medicalHistoryRepository.save(medicalHistory);
    }

    public ByteArrayOutputStream generateMedicalHistoryPdf(Long patientId) throws DocumentException, IOException {
        // Obtener el paciente por ID
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        // Obtener las citas médicas asociadas al paciente
        List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByPatient(patient);

        // Crear el documento PDF
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        // Agregar los detalles del paciente al PDF
        document.add(new Paragraph("Medical History of : " + patient.getUser().getName()));
        document.add(new Paragraph("Patient Document: " + patient.getUser().getDocument()));
        document.add(new Paragraph("\n"));

        if (medicalHistories.isEmpty()) {
            document.add(new Paragraph("No medical history available for this patient."));
        } else {
            // Agregar cada cita médica al PDF
            for (MedicalHistory history : medicalHistories) {
                Doctor doctor = history.getDoctor();
                document.add(new Paragraph("Doctor: " + doctor.getUser().getName()));
                document.add(new Paragraph("Date of appointment: " + history.getConsultationDate()));
                document.add(new Paragraph("Diagnosis: " + history.getDiagnosis()));
                document.add(new Paragraph("Treatment: " + history.getTreatment()));
                document.add(new Paragraph("Prescriptions: " + history.getPrescriptions()));
                document.add(new Paragraph("\n"));
            }
        }

        document.close();
        return byteArrayOutputStream;
    }
}


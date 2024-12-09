package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.domain.entities.Doctor;
import com.riwi.Hospital.domain.enums.MedicStatus;
import com.riwi.Hospital.infrastructure.persistence.AppointmentRepository;
import com.riwi.Hospital.infrastructure.persistence.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class DoctorStatusScheduler {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void updateDoctorStatus() {
        ZoneId colombiaZone = ZoneId.of("America/Bogota");
        LocalDateTime nowInColombia = LocalDateTime.now(colombiaZone);

        List<Doctor> doctors = doctorRepository.findAll();

        for (Doctor doctor : doctors) {
            List<LocalDateTime> futureAppointments = appointmentRepository
                    .findAppointmentTimesByDoctorIdAndAppointmentDateAfter(doctor.getId(), nowInColombia);

            boolean hasActiveAppointments = false;

            for (LocalDateTime appointmentTime : futureAppointments) {
                if (appointmentTime.isAfter(nowInColombia.minusMinutes(1)) && appointmentTime.isBefore(nowInColombia.plusMinutes(1))) {
                    hasActiveAppointments = true;
                    break;
                }
            }

            if (hasActiveAppointments) {
                doctor.setStatus(MedicStatus.UNAVAILABLE);
            } else {
                doctor.setStatus(MedicStatus.AVAILABLE);
            }

            doctorRepository.save(doctor);
        }
    }
}

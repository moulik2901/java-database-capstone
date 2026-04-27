package com.project.back_end.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.entity.Appointment;
import com.project.back_end.entity.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace(); // replace with logger in real apps
            return 0;
        }
    }

    // 2. Get Patient Appointments
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(patientId);

        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 3. Filter by Condition (past/future)
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByCondition(Long patientId, String condition) {

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(patientId);

        LocalDateTime now = LocalDateTime.now();

        return appointments.stream()
                .filter(a -> {
                    if ("future".equalsIgnoreCase(condition)) {
                        return a.getAppointmentTime().isAfter(now);
                    } else if ("past".equalsIgnoreCase(condition)) {
                        return a.getAppointmentTime().isBefore(now);
                    }
                    throw new RuntimeException("Invalid condition");
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 4. Filter by Doctor Name
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctor(Long patientId, String doctorName) {

        List<Appointment> appointments =
                appointmentRepository
                        .findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, doctorName);

        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 5. Filter by Doctor + Condition
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctorAndCondition(Long patientId,
                                                          String doctorName,
                                                          String condition) {

        List<Appointment> appointments =
                appointmentRepository
                        .findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, doctorName);

        LocalDateTime now = LocalDateTime.now();

        return appointments.stream()
                .filter(a -> {
                    if ("future".equalsIgnoreCase(condition)) {
                        return a.getAppointmentTime().isAfter(now);
                    } else if ("past".equalsIgnoreCase(condition)) {
                        return a.getAppointmentTime().isBefore(now);
                    }
                    throw new RuntimeException("Invalid condition");
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 6. Get Patient Details via Token
    public Patient getPatientDetails(String token) {

        String email = tokenService.extractEmail(token);

        Optional<Patient> optional = patientRepository.findByEmail(email);

        if (optional.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        return optional.get();
    }

    // Helper: Convert Entity → DTO
    private AppointmentDTO convertToDTO(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getPatient().getEmail(),
                a.getPatient().getPhone(),
                a.getPatient().getAddress(),
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}

package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.entity.Admin;
import com.project.back_end.entity.Doctor;
import com.project.back_end.entity.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AuthService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AuthService(TokenService tokenService,
                       AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       DoctorService doctorService,
                       PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1. Validate Token
    public ResponseEntity<?> validateToken(String token) {
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
        return ResponseEntity.ok("Valid token");
    }

    // 2. Validate Admin Login
    public ResponseEntity<?> validateAdmin(String username, String password) {
        try {
            Optional<Admin> optional = adminRepository.findByUsername(username);

            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username");
            }

            Admin admin = optional.get();

            if (!admin.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid password");
            }

            String token = tokenService.generateToken(admin.getUsername());

            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }

    // 3. Filter Doctors (Flexible)
    public List<Doctor> filterDoctor(String name, String specialty, String time) {

        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }

        if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        }

        if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }

        if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        }

        if (name != null) {
            return doctorService.findDoctorByName(name);
        }

        if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        }

        return doctorService.getDoctors();
    }

    // 4. Validate Appointment Slot
    public int validateAppointment(Long doctorId, LocalDate date, LocalTime time) {

        Optional<Doctor> optional = doctorRepository.findById(doctorId);

        if (optional.isEmpty()) {
            return -1;
        }

        List<LocalTime> availableSlots =
                doctorService.getDoctorAvailability(doctorId, date);

        return availableSlots.contains(time) ? 1 : 0;
    }

    // 5. Validate Patient (Uniqueness)
    public boolean validatePatient(String email, String phone) {
        return patientRepository.findByEmailOrPhone(email, phone).isEmpty();
    }

    // 6. Validate Patient Login
    public ResponseEntity<?> validatePatientLogin(String email, String password) {

        try {
            Optional<Patient> optional = patientRepository.findByEmail(email);

            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email");
            }

            Patient patient = optional.get();

            if (!patient.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid password");
            }

            String token = tokenService.generateToken(patient.getEmail());

            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }

    // 7. Filter Patient Appointments
    public Object filterPatient(String token, String condition, String doctorName) {

        String email = tokenService.extractEmail(token);

        Optional<Patient> optional = patientRepository.findByEmail(email);

        if (optional.isEmpty()) {
            return "Patient not found";
        }

        Long patientId = optional.get().getId();

        if (condition != null && doctorName != null) {
            return patientService.filterByDoctorAndCondition(patientId, doctorName, condition);
        }

        if (condition != null) {
            return patientService.filterByCondition(patientId, condition);
        }

        if (doctorName != null) {
            return patientService.filterByDoctor(patientId, doctorName);
        }

        return patientService.getPatientAppointment(patientId);
    }
}

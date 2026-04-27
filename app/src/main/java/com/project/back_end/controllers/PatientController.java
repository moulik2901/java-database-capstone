package com.project.back_end.controllers;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.entity.Patient;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.PatientService;

@RestController
@RequestMapping("${api.path}/patient")
public class PatientController {

    private final PatientService patientService;
    private final AuthService authService;

    public PatientController(PatientService patientService,
                             AuthService authService) {
        this.patientService = patientService;
        this.authService = authService;
    }

    // Helper: strip "Bearer " prefix
    private String extractToken(String header) {
        return (header != null && header.startsWith("Bearer "))
                ? header.substring(7)
                : header;
    }

    // 1. Get Patient Details (from token)
    @GetMapping("/me")
    public ResponseEntity<?> getPatient(@RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);

        if (!authService.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        return ResponseEntity.ok(patientService.getPatientDetails(token));
    }

    // 2. Register Patient
    @PostMapping
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {

        boolean isValid = authService.validatePatient(patient.getEmail(), patient.getPhone());

        if (!isValid) {
            return ResponseEntity.status(409)
                    .body("Patient with same email or phone already exists");
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            return ResponseEntity.ok("Patient created successfully");
        }

        return ResponseEntity.status(500).body("Error creating patient");
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        return authService.validatePatientLogin(login.getEmail(), login.getPassword());
    }

    // 4. Get Patient Appointments
    @GetMapping("/appointments")
    public ResponseEntity<?> getPatientAppointment(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);

        if (!authService.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // Delegate fully to service using token (safer than passing ID)
        return ResponseEntity.ok(
                Map.of("appointments",
                        authService.filterPatient(token, null, null))
        );
    }

    // 5. Filter Patient Appointments
    @GetMapping("/appointments/filter")
    public ResponseEntity<?> filterPatientAppointment(
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String doctorName,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);

        if (!authService.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        Object result = authService.filterPatient(token, condition, doctorName);

        return ResponseEntity.ok(Map.of("appointments", result));
    }
}

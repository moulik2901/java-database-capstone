package com.project.back_end.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.document.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.PrescriptionService;

@RestController
@RequestMapping("${api.path}/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final AuthService authService;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  AuthService authService,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.authService = authService;
        this.appointmentService = appointmentService;
    }

    // Helper to strip "Bearer "
    private String extractToken(String header) {
        return (header != null && header.startsWith("Bearer "))
                ? header.substring(7)
                : header;
    }

    // 1. Save Prescription (Doctor Only)
    @PostMapping
    public ResponseEntity<?> savePrescription(
            @RequestBody Prescription prescription,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);

        if (!authService.validateToken(token, "doctor").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Update appointment status (e.g., 1 = completed)
        appointmentService.changeStatus(prescription.getAppointmentId(), 1);

        return prescriptionService.savePrescription(prescription);
    }

    // 2. Get Prescription (Doctor Only)
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);

        if (!authService.validateToken(token, "doctor").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}

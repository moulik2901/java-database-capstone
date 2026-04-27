package com.project.back_end.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.entity.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuthService authService;

    public AppointmentController(AppointmentService appointmentService,
                                 AuthService authService) {
        this.appointmentService = appointmentService;
        this.authService = authService;
    }

    // 1. Get Appointments (Doctor View)
    @GetMapping("/doctor")
    public ResponseEntity<?> getAppointments(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String patientName,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "doctor").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        List<Appointment> appointments =
                appointmentService.getAppointments(doctorId, date, patientName);

        return ResponseEntity.ok(appointments);
    }

    // 2. Book Appointment
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        int result = appointmentService.bookAppointment(appointment);

        if (result == 1) {
            return ResponseEntity.ok("Appointment booked successfully");
        }

        return ResponseEntity.badRequest().body("Failed to book appointment");
    }

    // 3. Update Appointment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @RequestBody Appointment appointment,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String response =
                appointmentService.updateAppointment(id, appointment, appointment.getPatient().getId());

        return ResponseEntity.ok(response);
    }

    // 4. Cancel Appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // NOTE: ideally extract patientId from token instead
        String response =
                appointmentService.cancelAppointment(id, null);

        return ResponseEntity.ok(response);
    }
}

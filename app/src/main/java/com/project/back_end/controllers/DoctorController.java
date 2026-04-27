package com.project.back_end.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.entity.Doctor;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.DoctorService;

@RestController
@RequestMapping("${api.path}/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final AuthService authService;

    public DoctorController(DoctorService doctorService,
                            AuthService authService) {
        this.doctorService = doctorService;
        this.authService = authService;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability")
    public ResponseEntity<?> getDoctorAvailability(
            @RequestParam String role,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, role).getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        List<LocalTime> availableSlots =
                doctorService.getDoctorAvailability(doctorId, date);

        return ResponseEntity.ok(Map.of("availableSlots", availableSlots));
    }

    // 2. Get All Doctors
    @GetMapping
    public ResponseEntity<?> getDoctors() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    // 3. Save Doctor (Admin Only)
    @PostMapping
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "admin").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == -1) {
            return ResponseEntity.status(409).body("Doctor already exists");
        } else if (result == 1) {
            return ResponseEntity.ok("Doctor created successfully");
        }

        return ResponseEntity.status(500).body("Error creating doctor");
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login.getEmail(), login.getPassword());
    }

    // 5. Update Doctor
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "admin").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        int result = doctorService.updateDoctor(id, doctor);

        if (result == -1) {
            return ResponseEntity.status(404).body("Doctor not found");
        }

        return ResponseEntity.ok("Doctor updated successfully");
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        if (!authService.validateToken(token, "admin").getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        int result = doctorService.deleteDoctor(id);

        if (result == -1) {
            return ResponseEntity.status(404).body("Doctor not found");
        }

        return ResponseEntity.ok("Doctor deleted successfully");
    }

    // 7. Filter Doctors
    @GetMapping("/filter")
    public ResponseEntity<?> filterDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String time) {

        List<Doctor> doctors =
                authService.filterDoctor(name, specialty, time);

        return ResponseEntity.ok(Map.of("doctors", doctors));
    }
}

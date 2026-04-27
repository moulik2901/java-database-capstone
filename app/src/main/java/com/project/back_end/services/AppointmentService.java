package com.project.back_end.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.entity.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // Constructor Injection
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 1. Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 2. Update Appointment
    @Transactional
    public String updateAppointment(Long appointmentId, Appointment updatedAppointment, Long patientId) {

        Optional<Appointment> optional = appointmentRepository.findById(appointmentId);

        if (optional.isEmpty()) {
            return "Appointment not found";
        }

        Appointment existing = optional.get();

        // Validate patient ownership
        if (!existing.getPatient().getId().equals(patientId)) {
            return "Unauthorized action";
        }

        // Optional: Check doctor availability here (you can enhance later)

        existing.setAppointmentTime(updatedAppointment.getAppointmentTime());
        existing.setDoctor(updatedAppointment.getDoctor());

        appointmentRepository.save(existing);

        return "Appointment updated successfully";
    }

    // 3. Cancel Appointment
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {

        Optional<Appointment> optional = appointmentRepository.findById(appointmentId);

        if (optional.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = optional.get();

        // Validate patient ownership
        if (!appointment.getPatient().getId().equals(patientId)) {
            return "Unauthorized action";
        }

        appointmentRepository.delete(appointment);

        return "Appointment cancelled successfully";
    }

    // 4. Get Appointments (Doctor + Date + optional Patient Name)
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {

        if (patientName != null && !patientName.isEmpty()) {
            return appointmentRepository
                    .findByDoctorIdAndAppointmentDateAndPatientNameContainingIgnoreCase(
                            doctorId, date, patientName);
        }

        return appointmentRepository
                .findByDoctorIdAndAppointmentDate(doctorId, date);
    }

    // 5. Change Status
    @Transactional
    public String changeStatus(Long appointmentId, int status) {

        Optional<Appointment> optional = appointmentRepository.findById(appointmentId);

        if (optional.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = optional.get();
        appointment.setStatus(status);

        appointmentRepository.save(appointment);

        return "Status updated successfully";
    }
}

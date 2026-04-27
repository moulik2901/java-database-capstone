package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.entity.Appointment;
import com.project.back_end.entity.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Get Doctor Availability
    @Transactional(readOnly = true)
    public List<LocalTime> getDoctorAvailability(Long doctorId, LocalDate date) {

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);

        Set<LocalTime> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime())
                .collect(Collectors.toSet());

        // Example: 9 AM to 5 PM slots
        List<LocalTime> allSlots = new ArrayList<>();
        for (int i = 9; i < 17; i++) {
            allSlots.add(LocalTime.of(i, 0));
        }

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // 2. Save Doctor
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
                return -1; // conflict
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 3. Update Doctor
    @Transactional
    public int updateDoctor(Long id, Doctor updatedDoctor) {
        Optional<Doctor> optional = doctorRepository.findById(id);

        if (optional.isEmpty()) {
            return -1;
        }

        Doctor doctor = optional.get();
        doctor.setName(updatedDoctor.getName());
        doctor.setEmail(updatedDoctor.getEmail());
        doctor.setSpecialty(updatedDoctor.getSpecialty());

        doctorRepository.save(doctor);
        return 1;
    }

    // 4. Get All Doctors
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // 5. Delete Doctor
    @Transactional
    public int deleteDoctor(Long id) {
        Optional<Doctor> optional = doctorRepository.findById(id);

        if (optional.isEmpty()) {
            return -1;
        }

        appointmentRepository.deleteByDoctorId(id);
        doctorRepository.deleteById(id);

        return 1;
    }

    // 6. Validate Doctor Login
    public String validateDoctor(String email, String password) {

        Optional<Doctor> optional = doctorRepository.findByEmail(email);

        if (optional.isEmpty()) {
            return "Invalid email";
        }

        Doctor doctor = optional.get();

        if (!doctor.getPassword().equals(password)) {
            return "Invalid password";
        }

        return tokenService.generateToken(doctor.getId());
    }

    // 7. Find Doctor by Name
    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    // 8. Filter: Name + Specialty + Time
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String timePeriod) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        return filterDoctorByTime(doctors, timePeriod);
    }

    // 9. Filter by Time (AM/PM)
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timePeriod) {

        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes().stream().anyMatch(time -> {
                    int hour = time.getHour();
                    return ("AM".equalsIgnoreCase(timePeriod) && hour < 12)
                            || ("PM".equalsIgnoreCase(timePeriod) && hour >= 12);
                }))
                .collect(Collectors.toList());
    }

    // 10. Filter by Name + Time
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(String name, String timePeriod) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);
        return filterDoctorByTime(doctors, timePeriod);
    }

    // 11. Filter by Name + Specialty
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    // 12. Filter by Specialty + Time
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String timePeriod) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return filterDoctorByTime(doctors, timePeriod);
    }

    // 13. Filter by Specialty
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    // 14. Filter by Time (All Doctors)
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String timePeriod) {
        return filterDoctorByTime(doctorRepository.findAll(), timePeriod);
    }
}

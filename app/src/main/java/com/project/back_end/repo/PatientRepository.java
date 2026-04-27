package com.project.back_end.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by email
    Optional<Patient> findByEmail(String email);

    // Find patient by email OR phone
    Optional<Patient> findByEmailOrPhone(String email, String phone);
}

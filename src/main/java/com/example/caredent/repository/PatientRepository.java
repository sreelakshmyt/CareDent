package com.example.caredent.repository;

import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);   // <-- add this
}

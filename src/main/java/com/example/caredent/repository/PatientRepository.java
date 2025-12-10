package com.example.caredent.repository;

import com.example.caredent.bean.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByUserEmail(String email);  // custom query to find patient by linked user email
}

package com.example.caredent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByPatient(Patient patient); // check if patient already enrolled
     List<Enrollment> findByStatus(String status);
     List<Enrollment> findAllByPatient(Patient patient);

}

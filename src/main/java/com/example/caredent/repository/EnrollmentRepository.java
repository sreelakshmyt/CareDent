package com.example.caredent.repository;

<<<<<<< HEAD
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByPatient(Patient patient); // check if patient already enrolled
=======
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Enrollment;

public interface EnrollmentRepository  extends JpaRepository<Enrollment, Long> {
    
>>>>>>> 957a4df89d134d43af19539d1813e906f20d9610
}

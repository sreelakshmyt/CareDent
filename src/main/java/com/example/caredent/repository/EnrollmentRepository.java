package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Enrollment;

public interface EnrollmentRepository  extends JpaRepository<Enrollment, Long> {
    
}

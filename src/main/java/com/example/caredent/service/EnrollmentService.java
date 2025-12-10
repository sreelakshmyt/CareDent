package com.example.caredent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.caredent.bean.Enrollment;
import com.example.caredent.repository.EnrollmentRepository;

@Service
public class EnrollmentService {
    private final EnrollmentRepository repo;

    public EnrollmentService(EnrollmentRepository repo) {
        this.repo = repo;
    }

    public List<Enrollment> getPendingEnrollments() {
        return repo.findByStatus("PENDING");
    }

    public List<Enrollment> getAcceptedEnrollments() {
        return repo.findByStatus("ACCEPTED");
    }

    public List<Enrollment> getRejectedEnrollments() {
        return repo.findByStatus("REJECTED");
    }

    public void updateStatus(Long id, String status) {
        Enrollment e = repo.findById(id).orElseThrow();
        e.setStatus(status);
        repo.save(e);
    }
}

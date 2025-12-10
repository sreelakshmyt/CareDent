package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentalPlan;

public interface DentalPlanRepository extends JpaRepository<DentalPlan, Long> {
    
}

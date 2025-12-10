package com.example.caredent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.PlanCoverageRule;

public interface PlanCoverageRuleRepository extends JpaRepository<PlanCoverageRule, Long> {

    List<PlanCoverageRule> findAllByDentalPlan(DentalPlan dentalPlan);
    // You can add custom queries if needed
}

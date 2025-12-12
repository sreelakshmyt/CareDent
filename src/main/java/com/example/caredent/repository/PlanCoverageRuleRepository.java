package com.example.caredent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.PlanCoverageRule;

public interface PlanCoverageRuleRepository extends JpaRepository<PlanCoverageRule, Long> {

    List<PlanCoverageRule> findAllByDentalPlan(DentalPlan dentalPlan);
    // You can add custom queries if needed
    List<PlanCoverageRule> findByDentalPlan(DentalPlan dentalPlan);

      Optional<PlanCoverageRule> findByDentalPlanAndProcedureCategory(DentalPlan plan, String procedureCategory);
      Optional<PlanCoverageRule> findByDentalPlanIdAndProcedureCategory(Long id, String category);
      
 
}



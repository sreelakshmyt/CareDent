package com.example.caredent.service;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.ClaimLine;
import com.example.caredent.bean.DentalProcedure;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.PlanCoverageRule;
import com.example.caredent.bean.User;
import com.example.caredent.repository.ClaimLineRepository;
import com.example.caredent.repository.ClaimRepository;
import com.example.caredent.repository.DentalProcedureRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PlanCoverageRuleRepository;

@Service
public class ClaimService {

    @Autowired private ClaimRepository claimRepository;
    @Autowired private ClaimLineRepository claimLineRepository;
    @Autowired private DentalProcedureRepository dentalProcedureRepository;
    @Autowired private PlanCoverageRuleRepository planCoverageRuleRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;


        public Claim submitClaim(Patient patient, User dentist, List<DentalProcedure> procedures) {
        Claim claim = new Claim();
        claim.setPatient(patient);
        claim.setDentist(dentist);
        claim.setSubmissionDate(new Date());
        claim.setClaimStatus("PENDING");

        double total = 0.0;
        claim = claimRepository.save(claim);

        for (DentalProcedure proc : procedures) {
            ClaimLine line = new ClaimLine();
            line.setClaim(claim);
            line.setProcedureCode(proc.getProcedureCode());
            line.setAmount(proc.getStandardFee());
            claimLineRepository.save(line);
            total += proc.getStandardFee();
        }

        claim.setClaimAmount(total);
        return claimRepository.save(claim);
    }

    // public void approveClaim(Long claimId) {
    //     Claim claim = claimRepository.findById(claimId)
    //             .orElseThrow(() -> new RuntimeException("Claim not found"));

    //     // FIX: fetch enrollment via repository, not claim.getPatient().getEnrollment()
    //     Enrollment enrollment = enrollmentRepository.findActiveEnrollmentByPatient(claim.getPatient())
    //             .orElseThrow(() -> new RuntimeException("No active enrollment for patient"));

    //     double totalInsurancePaid = 0.0;
    //     double totalPatientResponsibility = 0.0;
    //     double totalDeductibleApplied = 0.0;

    //     List<ClaimLine> claimLines = claimLineRepository.findByClaim(claim);

    //     for (ClaimLine line : claimLines) {
    //         DentalProcedure procedure = dentalProcedureRepository.findByProcedureCode(line.getProcedureCode())
    //                 .orElseThrow(() -> new RuntimeException("Procedure not found"));

    //         PlanCoverageRule coverageRule = planCoverageRuleRepository.findByPlanAndCategory(
    //                 enrollment.getDentalPlan(), procedure.getCategory())
    //                 .orElseThrow(() -> new RuntimeException("Coverage rule not found"));

    //         double coveragePercentage = coverageRule.getCoveragePercentage();
    //         double insurancePaid = line.getAmount() * coveragePercentage / 100;
    //         double patientResponsibility = line.getAmount() - insurancePaid;

    //         // Deductible logic
    //         double deductibleApplied = 0.0;
    //         if (enrollment.getDeductibleUsed() < enrollment.getDentalPlan().getDeductible()) {
    //             double remainingDeductible = enrollment.getDentalPlan().getDeductible() - enrollment.getDeductibleUsed();
    //             deductibleApplied = Math.min(patientResponsibility, remainingDeductible);
    //             enrollment.setDeductibleUsed(enrollment.getDeductibleUsed() + deductibleApplied);
    //             patientResponsibility -= deductibleApplied;
    //         }

    //         // Update claim line
    //         line.setInsurancePaid(insurancePaid);
    //         line.setPatientResponsibility(patientResponsibility);
    //         line.setDeductibleApplied(deductibleApplied);
    //         claimLineRepository.save(line);

    //         totalInsurancePaid += insurancePaid;
    //         totalPatientResponsibility += patientResponsibility;
    //         totalDeductibleApplied += deductibleApplied;
    //     }

    //     // Update claim
    //     claim.setClaimStatus("APPROVED");
    //     claim.setInsurancePaid(totalInsurancePaid);
    //     claim.setPatientResponsibility(totalPatientResponsibility);
    //     claim.setApprovalDate(new Date());
    //     claimRepository.save(claim);

    //     // Update enrollment annual max
    //     enrollment.setAnnualMaxUsed(enrollment.getAnnualMaxUsed() + totalInsurancePaid);
    //     enrollmentRepository.save(enrollment);
    // }
}
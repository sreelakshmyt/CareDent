package com.example.caredent.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.ClaimLine;
import com.example.caredent.bean.DentalProcedure;
import com.example.caredent.bean.Enrollment;
import com.example.caredent.bean.PlanCoverageRule;
import com.example.caredent.dto.ClaimQueueItemDTO;
import com.example.caredent.repository.ClaimLineRepository;
import com.example.caredent.repository.ClaimRepository;
import com.example.caredent.repository.DentalProcedureRepository;
import com.example.caredent.repository.EnrollmentRepository;
import com.example.caredent.repository.PlanCoverageRuleRepository;

@Service
public class AdminClaimQueueService {

    private final ClaimRepository claimRepository;
    private final ClaimLineRepository claimLineRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DentalProcedureRepository procedureRepository;
    private final PlanCoverageRuleRepository coverageRuleRepository;

    public AdminClaimQueueService(
            ClaimRepository claimRepository,
            ClaimLineRepository claimLineRepository,
            EnrollmentRepository enrollmentRepository,
            DentalProcedureRepository procedureRepository,
            PlanCoverageRuleRepository coverageRuleRepository
    ) {
        this.claimRepository = claimRepository;
        this.claimLineRepository = claimLineRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.procedureRepository = procedureRepository;
        this.coverageRuleRepository = coverageRuleRepository;
    }

    public Page<ClaimQueueItemDTO> getPendingClaims(int page, int size) {
        Page<Claim> claims = claimRepository.findByClaimStatus("PENDING", PageRequest.of(page, size));
        return claims.map(this::toQueueItem);
    }

    private ClaimQueueItemDTO toQueueItem(Claim claim) {
        ClaimQueueItemDTO dto = new ClaimQueueItemDTO();
        dto.setId(claim.getId());
        String patientName = claim.getPatient() != null
                ? (Optional.ofNullable(claim.getPatient().getFirstName()).orElse("") + " " +
                   Optional.ofNullable(claim.getPatient().getLastName()).orElse("")).trim()
                : "Unknown";
        dto.setPatientName(patientName);
        String dentistName = claim.getDentist() != null ? claim.getDentist().getUsername() : "Unknown";
        dto.setDentistName(dentistName);
        dto.setSubmissionDate(claim.getSubmissionDate());
        dto.setClaimedAmount(Optional.ofNullable(claim.getClaimAmount()).orElse(0.0));
        dto.setStatus(claim.getClaimStatus());
        return dto;
    }

    @Transactional
    public Claim approveClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!"PENDING".equalsIgnoreCase(claim.getClaimStatus())) {
            throw new RuntimeException("Only PENDING claims can be approved");
        }

        Enrollment enrollment = enrollmentRepository.findByPatient(claim.getPatient())
                .orElseThrow(() -> new RuntimeException("Enrollment not found for patient"));

        double deductibleRemaining = Optional.ofNullable(enrollment.getDentalPlan().getDeductible()).orElse(0.0)
                - Optional.ofNullable(enrollment.getDeductibleUsed()).orElse(0.0);
        double annualMaxRemaining = Optional.ofNullable(enrollment.getDentalPlan().getAnnualMax()).orElse(0.0)
                - Optional.ofNullable(enrollment.getAnnualMaxUsed()).orElse(0.0);

        double totalInsurancePaid = 0.0;
        double totalPatientResp = 0.0;
        double totalDeductibleApplied = 0.0;

        List<ClaimLine> lines = claimLineRepository.findByClaim(claim);

        for (ClaimLine line : lines) {
            DentalProcedure proc = procedureRepository.findByProcedureCode(line.getProcedureCode())
                    .orElseThrow(() -> new RuntimeException("Procedure not found: " + line.getProcedureCode()));

            double fee = Optional.ofNullable(proc.getStandardFee()).orElse(Optional.ofNullable(line.getAmount()).orElse(0.0));

            double coverage = coverageRuleRepository
                    .findByDentalPlanAndProcedureCategory(enrollment.getDentalPlan(), proc.getCategory())
                    .map(PlanCoverageRule::getCoveragePercentage)
                    .orElse(0.0);

            double deductibleApplied = Math.min(fee, Math.max(deductibleRemaining, 0.0));
            deductibleRemaining -= deductibleApplied;

            double coveredAmount = (fee - deductibleApplied) * (coverage / 100.0);
            double insurancePaid = Math.min(coveredAmount, Math.max(annualMaxRemaining, 0.0));
            annualMaxRemaining -= insurancePaid;

            double patientResponsibility = fee - insurancePaid;

            line.setInsurancePaid(insurancePaid);
            line.setPatientResponsibility(patientResponsibility);
            line.setDeductibleApplied(deductibleApplied);
            claimLineRepository.save(line);

            totalInsurancePaid += insurancePaid;
            totalPatientResp += patientResponsibility;
            totalDeductibleApplied += deductibleApplied;
        }

        claim.setInsurancePaid(totalInsurancePaid);
        claim.setPatientResponsibility(totalPatientResp);
        claim.setClaimAmount(totalInsurancePaid); // if claimAmount reflects payout; otherwise keep original
        claim.setClaimStatus("APPROVED");
        claim.setApprovalDate(new Date());
        claim.setRejectionReason(null);
        claimRepository.save(claim);

        enrollment.setDeductibleUsed(Optional.ofNullable(enrollment.getDeductibleUsed()).orElse(0.0) + totalDeductibleApplied);
        enrollment.setAnnualMaxUsed(Optional.ofNullable(enrollment.getAnnualMaxUsed()).orElse(0.0) + totalInsurancePaid);
        enrollmentRepository.save(enrollment);

        return claim;
    }

    @Transactional
    public Claim rejectClaim(Long claimId, String reason) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!"PENDING".equalsIgnoreCase(claim.getClaimStatus())) {
            throw new RuntimeException("Only PENDING claims can be rejected");
        }

        claim.setClaimStatus("REJECTED");
        claim.setApprovalDate(null);
        claim.setRejectionReason(reason);
        return claimRepository.save(claim);
    }

    @Transactional
    public int approveAllPending() {
        Page<Claim> page = claimRepository.findByClaimStatus("PENDING", PageRequest.of(0, 500)); // batch size
        int processed = 0;
        for (Claim c : page.getContent()) {
            approveClaim(c.getId());
            processed++;
        }
        return processed;
    }
}

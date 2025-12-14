package com.example.caredent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Important: Added for @Param

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.ClaimLine;

public interface ClaimLineRepository extends JpaRepository<ClaimLine, Long> {
    
    // Existing method:
    List<ClaimLine> findByClaim(Claim claim);

    // --- NEW METHOD FOR BENEFIT USAGE SUMMARY ---

    /**
     * Aggregates the total deductible amount applied across all claim lines 
     * for a specific patient from claims that have been 'APPROVED'. This is used 
     * to track the total deductible met by the patient.
     */
    @Query("SELECT COALESCE(SUM(cl.deductibleApplied), 0) FROM ClaimLine cl JOIN cl.claim c WHERE c.patient.id = :patientId AND c.claimStatus = 'APPROVED'")
    Double sumDeductibleAppliedByPatientIdAndStatus(@Param("patientId") Long patientId);
}
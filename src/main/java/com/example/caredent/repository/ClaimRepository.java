package com.example.caredent.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Important: Added for @Param

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    // Existing methods:
    List<Claim> findByClaimStatus(String claimStatus);

    List<Claim> findByDentist(User user);
    List<Claim> findAllByPatient(Patient patient);

    Page<Claim> findByClaimStatus(String claimStatus, Pageable pageable);

    long countByClaimStatus(String claimStatus);

    @Query("SELECT COALESCE(SUM(c.insurancePaid), 0) FROM Claim c WHERE c.claimStatus = :status")
    Double sumInsurancePaidByStatus(@Param("status") String status);

    @Query("SELECT COALESCE(SUM(c.patientResponsibility), 0) FROM Claim c WHERE c.claimStatus = :status")
    Double sumPatientRespByStatus(@Param("status") String status);

    @Query("SELECT COALESCE(COUNT(c), 0) FROM Claim c")
    long countAllClaims();
    
    // --- NEW METHOD FOR BENEFIT USAGE SUMMARY ---
    
    /**
     * Aggregates the total amount the insurance has paid for a specific patient 
     * based on claims that have been 'APPROVED'. This is used to track Annual Maximum usage.
     */
    @Query("SELECT COALESCE(SUM(c.insurancePaid), 0) FROM Claim c WHERE c.patient.id = :patientId AND c.claimStatus = 'APPROVED'")
    Double sumInsurancePaidByPatientIdAndStatus(@Param("patientId") Long patientId);
}
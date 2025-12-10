package com.example.caredent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimStatus(String claimStatus);
    
}

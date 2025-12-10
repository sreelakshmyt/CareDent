package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
}

package com.example.caredent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Claim;
import com.example.caredent.bean.ClaimLine;

public interface ClaimLineRepository extends JpaRepository<ClaimLine, Long> {
     List<ClaimLine> findByClaim(Claim claim);

}

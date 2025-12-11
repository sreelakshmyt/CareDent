package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.ClaimLine;

public interface ClaimLineRepository extends JpaRepository<ClaimLine, Long> {

}

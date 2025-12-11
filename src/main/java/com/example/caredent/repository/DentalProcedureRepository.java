package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentalProcedure;

public interface DentalProcedureRepository extends JpaRepository<DentalProcedure, Long> {

}

package com.example.caredent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentalProcedure;
import com.example.caredent.bean.User;

public interface DentalProcedureRepository extends JpaRepository<DentalProcedure, Long> {
     List<DentalProcedure> findByDentist(User dentist);

     Optional<DentalProcedure> findByProcedureCode(String procedureCode);
}

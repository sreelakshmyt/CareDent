package com.example.caredent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.DentistNetwork;

public interface DentistNetworkRepository extends JpaRepository<DentistNetwork, Long> {

    List<DentistNetwork> findByDentalPlan(DentalPlan plan);
    
}

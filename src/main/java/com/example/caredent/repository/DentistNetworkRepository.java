package com.example.caredent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentalPlan;
import com.example.caredent.bean.DentistNetwork;
import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;

public interface DentistNetworkRepository extends JpaRepository<DentistNetwork, Long> {

    List<DentistNetwork> findByDentalPlan(DentalPlan plan);

    Optional<Patient> findByDentist(User user);
    
}

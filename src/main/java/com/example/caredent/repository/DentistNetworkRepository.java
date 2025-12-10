package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.DentistNetwork;

public interface DentistNetworkRepository extends JpaRepository<DentistNetwork, Long> {
    
}

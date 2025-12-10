package com.example.caredent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.caredent.bean.Network;

public interface NetworkRepository extends JpaRepository<Network, Long> {
    
}

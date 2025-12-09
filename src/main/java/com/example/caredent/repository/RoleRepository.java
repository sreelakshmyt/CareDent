package com.example.caredent.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.caredent.bean.Role;
@Repository

public interface RoleRepository extends JpaRepository<Role,Long>{

    Optional<Role> findByName(String string);

}

package com.example.caredent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.caredent.bean.Role;
import com.example.caredent.bean.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

   
    // Query to find users by their role
    List<User> findByRole(Role role);
    List<User> findByRoleName(String roleName);


}

// src/main/java/com/example/caredent/service/ProfileManagementService.java

package com.example.caredent.service;

import com.example.caredent.bean.Patient;
import com.example.caredent.bean.User;
import com.example.caredent.dto.ProfileForm;
import com.example.caredent.repository.PatientRepository;
import com.example.caredent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Patientprofilemanage {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;
    
    // (A PasswordEncoder dependency would typically be injected here)

    public ProfileForm getProfileData(User user) {
        Patient patient = patientRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Patient profile not found."));
        
        ProfileForm form = new ProfileForm();
        
        // Map data from User and Patient entities to the minimalist ProfileForm DTO
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setFirstName(patient.getFirstName());
        form.setLastName(patient.getLastName());
        form.setPhone(patient.getPhone());
        form.setAddress(patient.getAddress());
        form.setCity(patient.getCity());
        form.setState(patient.getState());
        form.setZipCode(patient.getZipCode());
        
        return form;
    }

    /**
     * Updates profile details and handles password change if requested.
     * @return true if successful (details updated OR password changed), false if current password validation fails.
     */
    public boolean updateProfile(User user, ProfileForm form) {
        Patient patient = patientRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Patient not found."));

        // 1. Update Basic User/Patient Contact Details
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        
        patient.setFirstName(form.getFirstName());
        patient.setLastName(form.getLastName());
        patient.setPhone(form.getPhone());
        patient.setAddress(form.getAddress());
        patient.setCity(form.getCity());
        patient.setState(form.getState());
        patient.setZipCode(form.getZipCode());
        
        // 2. Handle Password Change Request
        if (form.getNewPassword() != null && !form.getNewPassword().isEmpty()) {
            
            // Validation 1: Check if new passwords match
            if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
                throw new IllegalArgumentException("New password and confirmation do not match.");
            }

            // Validation 2: Verify Current Password (MOCK for demo)
            // In a real app, this should involve passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())
            boolean currentPasswordMatches = true; // Placeholder for security check
            
            if (form.getCurrentPassword() == null || form.getCurrentPassword().isEmpty() || !currentPasswordMatches) {
                // If current password isn't provided or doesn't match the database password
                return false; 
            }
            
            // If current password is valid, encrypt and set the new password
            user.setPassword(form.getNewPassword()); // WARNING: Save Encoded password in real app!
        }
        
        userRepository.save(user);
        patientRepository.save(patient);
        return true;
    }
}
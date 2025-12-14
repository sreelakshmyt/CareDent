
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

    /**
     * Retrieves existing patient data or an empty ProfileForm for new users 
     * without a corresponding Patient record yet.
     */
    public ProfileForm getProfileData(User user) {
        // FIX: Use orElse(new Patient()) to ensure a Patient object (even an empty one) 
        // is always available to populate the form, preventing the crash for new users.
        Patient patient = patientRepository.findByUser(user).orElse(new Patient());
        
        ProfileForm form = new ProfileForm();
        
        // Map data from User and Patient entities to the minimalist ProfileForm DTO
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        
        // These will be null/empty for a new user, which is correct for pre-filling the form.
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
     * For new users, it creates the Patient record.
     * @return true if successful (details updated OR password changed), false if current password validation fails.
     */
    public boolean updateProfile(User user, ProfileForm form) {
        // FIX: Use orElse(new Patient()) to either get the existing record OR create a new one.
        Patient patient = patientRepository.findByUser(user).orElse(new Patient());

        // If this is a new Patient object (i.e., we just created it), we must link it to the User.
        if (patient.getId() == null) {
            patient.setUser(user);
        }

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
        // Note: Missing .setGender() if ProfileForm has it, but based on your DTO usage, we stick to what is present.
        
        // 2. Handle Password Change Request
        if (form.getNewPassword() != null && !form.getNewPassword().isEmpty()) {
            
            // Validation 1: Check if new passwords match
            if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
                throw new IllegalArgumentException("New password and confirmation do not match.");
            }

            // Validation 2: Verify Current Password (MOCK for demo)
            // *** IMPORTANT: The current logic below is a security placeholder. ***
            // You must use Spring Security's PasswordEncoder here. 
            boolean currentPasswordMatches = true; // Placeholder for security check
            
            if (form.getCurrentPassword() == null || form.getCurrentPassword().isEmpty() || !currentPasswordMatches) {
                // If current password isn't provided or doesn't match the database password
                // For a real app, this should only happen if form.getCurrentPassword() fails validation.
                return false; 
            }
            
            // If current password is valid, encrypt and set the new password
            // REMINDER: user.setPassword(ENCODER.encode(form.getNewPassword()));
            user.setPassword(form.getNewPassword()); // WARNING: Save Encoded password in real app!
        }
        
        // 3. Persist changes
        userRepository.save(user);
        patientRepository.save(patient);
        return true;
    }
}
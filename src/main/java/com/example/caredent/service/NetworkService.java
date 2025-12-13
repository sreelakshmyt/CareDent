package com.example.caredent.service;

import com.example.caredent.bean.DentistNetwork;
import com.example.caredent.bean.DentalPlan; // Required for fetching plan
import com.example.caredent.bean.Doctor;
import com.example.caredent.repository.DentistNetworkRepository;
import com.example.caredent.repository.DentalPlanRepository; // Required for fetching plan
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkService {

    @Autowired
    private DentistNetworkRepository dentistNetworkRepository;

    @Autowired
    private DentalPlanRepository dentalPlanRepository; // Inject to fetch the plan object

    /**
     * Simulates external lookup of Doctor professional details based on User ID.
     * Includes five different doctor profiles.
     */
    private Doctor getSimulatedDoctorDetails(Long userId) {
        
        // Dentist 1: General Practitioner (User ID 3)
        if (userId.equals(3L)) {
            return new Doctor(3L, "Dr. Priya Sharma", "Greenwood Family Dentistry", "General Dentistry", "DDS");
        }
        
        // Dentist 2: General Practitioner (User ID 6)
        if (userId.equals(6L)) {
            return new Doctor(6L, "Dr. Alice Chen", "City Dental Center", "General Dentistry", "DMD");
        }
        
        // Dentist 3: Specialist (Orthodontist) (User ID 7)
        if (userId.equals(7L)) {
            return new Doctor(7L, "Dr. Robert Smith", "Premier Orthodontics", "Orthodontics", "DDS, MS");
        }
        
        // Dentist 4: Specialist (Oral Surgeon) (User ID 8 - NEW)
        if (userId.equals(8L)) {
            return new Doctor(8L, "Dr. Michael Jones", "Advanced Oral Surgery", "Oral Surgery", "DDS, MD");
        }
        
        // Dentist 5: Pediatric Dentist (User ID 9 - NEW)
        if (userId.equals(9L)) {
            return new Doctor(9L, "Dr. Emily WONG", "Tiny Teeth Clinic", "Pediatric Dentistry", "DMD");
        }
        
        return new Doctor(userId, "Unknown Provider (ID: " + userId + ")", "Local Clinic", "General Dentistry", "DDS");
    }


    /**
     * Fetches in-network doctors by querying the DentistNetwork table using the DentalPlan object.
     */
    public List<Doctor> findInNetworkDoctors(Long planId) {
        if (planId == null) {
            return List.of();
        }

        // 1. Fetch the DentalPlan entity using the ID
        DentalPlan plan = dentalPlanRepository.findById(planId)
            .orElse(null);

        if (plan == null) {
            return List.of();
        }

        // 2. Query the database using the concise repository method: findByDentalPlan
        // Note: This relies on the controller also having access to the DentalPlanRepository
        List<DentistNetwork> networks = dentistNetworkRepository.findByDentalPlan(plan);

        // 3. Map the results to the Doctor DTO
        return networks.stream()
                .map(network -> {
                    Long dentistUserId = network.getDentist().getId();
                    
                    // Get professional details using the real User ID
                    Doctor simulatedDetails = getSimulatedDoctorDetails(dentistUserId);
                    
                    // Return the final Doctor DTO for the view
                    return new Doctor(
                        dentistUserId,
                        simulatedDetails.getName(),
                        simulatedDetails.getHospitalName(),
                        simulatedDetails.getSpecialty(),
                        simulatedDetails.getQualification()
                    );
                })
                .collect(Collectors.toList());
    }
}

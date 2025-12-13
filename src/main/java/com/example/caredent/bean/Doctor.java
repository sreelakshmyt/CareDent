package com.example.caredent.bean;



public class Doctor {
    
    private Long id;
    private String name;
    private String hospitalName;
    private String specialty;
    private String qualification;

    // Constructor to easily create doctor objects with all fields
    public Doctor(Long id, String name, String hospitalName, String specialty, String qualification) {
        this.id = id;
        this.name = name;
        this.hospitalName = hospitalName;
        this.specialty = specialty;
        this.qualification = qualification;
    }

    // Getters 
    public Long getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getHospitalName() { 
        return hospitalName; 
    }
    
    public String getSpecialty() { 
        return specialty; 
    }
    
    public String getQualification() { 
        return qualification; 
    }
    
    // Setters are typically not needed for a read-only DTO like this, 
    // but you can add them if necessary for future use.
}

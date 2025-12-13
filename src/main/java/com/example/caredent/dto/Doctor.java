package com.example.caredent.dto;



public class Doctor {
    private Long id;
    private String name;
    private String hospitalName;
    private String specialty;
    private String qualification;

    public Doctor(Long id, String name, String hospitalName, String specialty, String qualification) {
        this.id = id;
        this.name = name;
        this.hospitalName = hospitalName;
        this.specialty = specialty;
        this.qualification = qualification;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getHospitalName() { return hospitalName; }
    public String getSpecialty() { return specialty; }
    public String getQualification() { return qualification; }
    // (You can omit setters as this is used only for display)
}
package com.example.caredent.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private DentalPlan dentalPlan;

    private Integer benefitYear;

    private Double deductibleUsed = 0.0;

    private Double annualMaxUsed = 0.0;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public DentalPlan getDentalPlan() {
        return dentalPlan;
    }

    public void setDentalPlan(DentalPlan dentalPlan) {
        this.dentalPlan = dentalPlan;
    }

    public Integer getBenefitYear() {
        return benefitYear;
    }

    public void setBenefitYear(Integer benefitYear) {
        this.benefitYear = benefitYear;
    }

    public Double getDeductibleUsed() {
        return deductibleUsed;
    }

    public void setDeductibleUsed(Double deductibleUsed) {
        this.deductibleUsed = deductibleUsed;
    }

    public Double getAnnualMaxUsed() {
        return annualMaxUsed;
    }

    public void setAnnualMaxUsed(Double annualMaxUsed) {
        this.annualMaxUsed = annualMaxUsed;
    }
}


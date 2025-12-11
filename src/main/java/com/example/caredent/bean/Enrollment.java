package com.example.caredent.bean;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity

public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Patient
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    // Link to DentalPlan
    @ManyToOne
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private DentalPlan dentalPlan;
    //private String status; // e.g., ACTIVE, CANCELLED

    // Benefit period
    private LocalDate benefitYearStart;
    private LocalDate benefitYearEnd;

    // Usage tracking
    private Double deductibleUsed = 0.0;
    private Double annualMaxUsed = 0.0;

    private String status="PENDING"; // PENDING, ACCEPTED, REJECTED
    


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public DentalPlan getDentalPlan() { return dentalPlan; }
    public void setDentalPlan(DentalPlan dentalPlan) { this.dentalPlan = dentalPlan; }

    public LocalDate getBenefitYearStart() { return benefitYearStart; }
    public void setBenefitYearStart(LocalDate benefitYearStart) { this.benefitYearStart = benefitYearStart; }

    public LocalDate getBenefitYearEnd() { return benefitYearEnd; }
    public void setBenefitYearEnd(LocalDate benefitYearEnd) { this.benefitYearEnd = benefitYearEnd; }

    public Double getDeductibleUsed() { return deductibleUsed; }
    public void setDeductibleUsed(Double deductibleUsed) { this.deductibleUsed = deductibleUsed; }

    public Double getAnnualMaxUsed() { return annualMaxUsed; }
    public void setAnnualMaxUsed(Double annualMaxUsed) { this.annualMaxUsed = annualMaxUsed; }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

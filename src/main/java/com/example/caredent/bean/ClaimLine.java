package com.example.caredent.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ClaimLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "claim_id", referencedColumnName = "id")
    private Claim claim;

    private String procedureCode;

    private Double amount;

        // ClaimLine.java (add fields)
    private Double insurancePaid;
    private Double patientResponsibility;
    private Double deductibleApplied;
    

// getters/setters...


    public Double getInsurancePaid() {
        return insurancePaid;
    }

    public void setInsurancePaid(Double insurancePaid) {
        this.insurancePaid = insurancePaid;
    }

    public Double getPatientResponsibility() {
        return patientResponsibility;
    }

    public void setPatientResponsibility(Double patientResponsibility) {
        this.patientResponsibility = patientResponsibility;
    }

    public Double getDeductibleApplied() {
        return deductibleApplied;
    }

    public void setDeductibleApplied(Double deductibleApplied) {
        this.deductibleApplied = deductibleApplied;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}


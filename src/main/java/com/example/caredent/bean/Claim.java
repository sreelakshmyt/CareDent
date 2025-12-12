package com.example.caredent.bean;




import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "dentist_id", referencedColumnName = "id")
    private User dentist;

    private Double claimAmount;

    private String claimStatus;

    private Date submissionDate;
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ClaimLine> claimLines = new ArrayList<>();

// Getter and Setter
public List<ClaimLine> getClaimLines() { return claimLines; }
public void setClaimLines(List<ClaimLine> claimLines) { this.claimLines = claimLines; }


    // Claim.java (add fields)
    private Double insurancePaid;        // total insurance payout for the claim
    private Double patientResponsibility; // total patient responsibility
    private Date approvalDate;           // set on approve
    private String rejectionReason;      // set on reject

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

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

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

    public User getDentist() {
        return dentist;
    }

    public void setDentist(User dentist) {
        this.dentist = dentist;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
}

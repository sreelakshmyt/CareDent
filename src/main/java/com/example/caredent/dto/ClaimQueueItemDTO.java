package com.example.caredent.dto;

import java.util.Date;

public class ClaimQueueItemDTO {
    private Long id;
    private String patientName;
    private String dentistName;
    private Date submissionDate;
    private Double claimedAmount; // sum of line amounts (or Claim.claimAmount)
    private String status;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    public String getDentistName() {
        return dentistName;
    }
    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }
    public Date getSubmissionDate() {
        return submissionDate;
    }
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    public Double getClaimedAmount() {
        return claimedAmount;
    }
    public void setClaimedAmount(Double claimedAmount) {
        this.claimedAmount = claimedAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    

    // getters/setters...
}

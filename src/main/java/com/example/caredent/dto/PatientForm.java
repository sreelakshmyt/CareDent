package com.example.caredent.dto;
import java.time.LocalDate;

public class PatientForm {
    private Long planId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String ssn;
    private String address;
    private String phone;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    private String email;  

// Enrollment info
    private String coverageType;
    private String dependents; // for demo, free-text; or List<DependentDTO>
    private String otherInsurance;
    private String carrierName;
    private String policyNumber;

    private String dependentName;
private LocalDate dependentDob;
private String dependentRelationship;
private String dependentGender;
private Boolean dependentStudent;
private Boolean dependentDisabled;




    public String getDependentName() {
    return dependentName;
}
public void setDependentName(String dependentName) {
    this.dependentName = dependentName;
}
public LocalDate getDependentDob() {
    return dependentDob;
}
public void setDependentDob(LocalDate dependentDob) {
    this.dependentDob = dependentDob;
}
public String getDependentRelationship() {
    return dependentRelationship;
}
public void setDependentRelationship(String dependentRelationship) {
    this.dependentRelationship = dependentRelationship;
}
public String getDependentGender() {
    return dependentGender;
}
public void setDependentGender(String dependentGender) {
    this.dependentGender = dependentGender;
}
public Boolean getDependentStudent() {
    return dependentStudent;
}
public void setDependentStudent(Boolean dependentStudent) {
    this.dependentStudent = dependentStudent;
}
public Boolean getDependentDisabled() {
    return dependentDisabled;
}
public void setDependentDisabled(Boolean dependentDisabled) {
    this.dependentDisabled = dependentDisabled;
}
    // Consent
    private Boolean consentAccepted;


    public String getCoverageType() {
        return coverageType;
    }
    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }
    public String getDependents() {
        return dependents;
    }
    public void setDependents(String dependents) {
        this.dependents = dependents;
    }
    public String getOtherInsurance() {
        return otherInsurance;
    }
    public void setOtherInsurance(String otherInsurance) {
        this.otherInsurance = otherInsurance;
    }
    public String getCarrierName() {
        return carrierName;
    }
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    public Boolean getConsentAccepted() {
        return consentAccepted;
    }
    public void setConsentAccepted(Boolean consentAccepted) {
        this.consentAccepted = consentAccepted;
    }
    // getters/setters
    public Long getPlanId() {
        return planId;
    }
    public void setPlanId(Long planId) {
        this.planId = planId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public LocalDate getDob() {
        return dob;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public String getSsn() {
        return ssn;
    }
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}

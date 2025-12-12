package com.example.caredent.dto;
import java.time.LocalDate;

public class PatientForm {

    // 1. Plan ID
    private Long planId;

    // 2. Patient Personal Info
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender; // <--- NEW FIELD
    private String ssn;
    private String address; // Retained as Address Line 1
    private String city;    // <--- NEW FIELD
    private String state;   // <--- NEW FIELD
    private String zipCode; // <--- NEW FIELD
    private String phone;
    private String email; 

    // 3. Enrollment Info
    private String coverageType;
    private String dependents; 
    private String otherInsurance;
    private String carrierName;
    private String policyNumber;

    // 4. Single Dependent Details 
    private String dependentName;
    private LocalDate dependentDob;
    private String dependentRelationship;
    private String dependentGender;
    private Boolean dependentStudent;
    private Boolean dependentDisabled;

    // 5. Consent
    private Boolean consentAccepted;

    
    // --- Getters and Setters ---

    // Patient Info Getters/Setters
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    
    // NEW GETTER/SETTER for Gender
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    // NEW GETTERS/SETTERS for detailed Address
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; } 
    public void setEmail(String email) { this.email = email; } 

    // Enrollment Info Getters/Setters
    public String getCoverageType() { return coverageType; }
    public void setCoverageType(String coverageType) { this.coverageType = coverageType; }
    public String getDependents() { return dependents; }
    public void setDependents(String dependents) { this.dependents = dependents; }
    public String getOtherInsurance() { return otherInsurance; }
    public void setOtherInsurance(String otherInsurance) { this.otherInsurance = otherInsurance; }
    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    // Dependent Getters/Setters
    public String getDependentName() { return dependentName; }
    public void setDependentName(String dependentName) { this.dependentName = dependentName; }
    public LocalDate getDependentDob() { return dependentDob; }
    public void setDependentDob(LocalDate dependentDob) { this.dependentDob = dependentDob; }
    public String getDependentRelationship() { return dependentRelationship; }
    public void setDependentRelationship(String dependentRelationship) { this.dependentRelationship = dependentRelationship; }
    public String getDependentGender() { return dependentGender; }
    public void setDependentGender(String dependentGender) { this.dependentGender = dependentGender; }
    public Boolean getDependentStudent() { return dependentStudent; }
    public void setDependentStudent(Boolean dependentStudent) { this.dependentStudent = dependentStudent; }
    public Boolean getDependentDisabled() { return dependentDisabled; }
    public void setDependentDisabled(Boolean dependentDisabled) { this.dependentDisabled = dependentDisabled; }
    
    // Consent Getter/Setter
    public Boolean getConsentAccepted() { return consentAccepted; }
    public void setConsentAccepted(Boolean consentAccepted) { this.consentAccepted = consentAccepted; }
}
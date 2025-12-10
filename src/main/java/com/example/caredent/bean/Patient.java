package com.example.caredent.bean;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity

public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to User (login credentials)
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Demographic details
    public String firstName;
    public String lastName;
    private LocalDate dob;
    private String ssn;
    private String address;
    private String phone;

    // Tracking usage (optional, can also be in Enrollment)
    private Double deductibleUsed = 0.0;
    private Double annualMaxUsed = 0.0;

    // Getters and Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getDeductibleUsed() { return deductibleUsed; }
    public void setDeductibleUsed(Double deductibleUsed) { this.deductibleUsed = deductibleUsed; }

    public Double getAnnualMaxUsed() { return annualMaxUsed; }
    public void setAnnualMaxUsed(Double annualMaxUsed) { this.annualMaxUsed = annualMaxUsed; }
}

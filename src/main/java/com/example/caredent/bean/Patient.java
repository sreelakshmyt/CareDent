package com.example.caredent.bean;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity

public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long planId;  // You can use a separate Plan object here, but for simplicity, it's just an ID
    private Double deductibleUsed = 0.0;
    private Double annualMaxUsed = 0.0;

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
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

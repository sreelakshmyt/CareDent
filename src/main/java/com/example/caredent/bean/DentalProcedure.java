package com.example.caredent.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DentalProcedure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String procedureCode;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    private Double standardFee;

    @ManyToOne
    @JoinColumn(name = "dentist_id", referencedColumnName = "id")
    private User dentist; 
    public User getDentist() {
        return dentist;
    }
    public void setDentist(User dentist) {
        this.dentist = dentist;
    }
    public Double getStandardFee() {
        return standardFee;
    }
    public void setStandardFee(Double standardFee) {
        this.standardFee = standardFee;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

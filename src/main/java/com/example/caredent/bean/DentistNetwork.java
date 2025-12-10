package com.example.caredent.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DentistNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dentist_id", referencedColumnName = "id")
    private User dentist;

    // @ManyToOne
    // @JoinColumn(name = "network_id", referencedColumnName = "id")
    // private Network network;

    @ManyToOne
    @JoinColumn(name = "dentalplan_id", referencedColumnName = "id")
    private DentalPlan dentalPlan;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDentist() {
        return dentist;
    }

    public void setDentist(User dentist) {
        this.dentist = dentist;
    }
    public DentalPlan getDentalPlan() {
        return dentalPlan;
    }
    public void setDentalPlan(DentalPlan dentalPlan) {
        this.dentalPlan = dentalPlan;
    }
    

    // public Network getNetwork() {
    //     return network;
    // }

    // public void setNetwork(Network network) {
    //     this.network = network;
    // }
}

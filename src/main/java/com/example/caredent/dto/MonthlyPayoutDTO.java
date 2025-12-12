package com.example.caredent.dto;



public class MonthlyPayoutDTO {
    private int month; // 1-12
    private Double payout;
    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public Double getPayout() {
        return payout;
    }
    public void setPayout(Double payout) {
        this.payout = payout;
    }
    // getters/setters...

    
}

// src/main/java/com/example/caredent/dto/CancellationRequestForm.java
package com.example.caredent.dto;

import java.time.LocalDate;

public class CancellationRequestForm {
    private Long enrollmentId;
    private String reason;

    // Getters and Setters
    public Long getEnrollmentId() {
        return enrollmentId;
    }
    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}

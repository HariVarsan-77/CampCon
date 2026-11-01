package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectionRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(min = 10, max = 1000, message = "Rejection reason must be at least 10 characters")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}


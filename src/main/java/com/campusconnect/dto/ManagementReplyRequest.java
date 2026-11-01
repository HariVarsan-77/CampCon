package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManagementReplyRequest {

    @NotBlank(message = "Response message cannot be empty")
    @Size(min = 5, max = 2000, message = "Response message must be between 5 and 2000 characters")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


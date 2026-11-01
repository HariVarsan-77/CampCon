package com.campusconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InternalNoteRequest {

    @NotBlank(message = "Internal note cannot be empty")
    @Size(min = 3, max = 1500, message = "Note must be between 3 and 1500 characters")
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}


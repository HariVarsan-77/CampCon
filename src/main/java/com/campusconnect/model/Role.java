package com.campusconnect.model;

public enum Role {
    ROLE_STUDENT("Student"),
    ROLE_MANAGEMENT("Management");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


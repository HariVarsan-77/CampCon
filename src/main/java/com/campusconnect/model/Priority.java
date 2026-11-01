package com.campusconnect.model;

public enum Priority {
    LOW("Low", "priority-low"),
    MEDIUM("Medium", "priority-medium"),
    HIGH("High", "priority-high"),
    CRITICAL("Critical", "priority-critical");

    private final String displayName;
    private final String badgeClass;

    Priority(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}


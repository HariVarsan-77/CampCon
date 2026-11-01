package com.campusconnect.model;

public enum ComplaintStatus {
    SUBMITTED("Submitted", "status-submitted", 1, "Received and logged in system"),
    UNDER_REVIEW("Under Review", "status-review", 2, "Management is assessing the complaint"),
    ACTION_REQUIRED("Action Required", "status-action", 3, "Action scheduled / assigned to campus staff"),
    RESOLVED("Resolved", "status-resolved", 4, "Issue has been addressed and verified"),
    REJECTED("Rejected", "status-rejected", -1, "Complaint rejected with reason");

    private final String displayName;
    private final String badgeClass;
    private final int stepIndex;
    private final String description;

    ComplaintStatus(String displayName, String badgeClass, int stepIndex, String description) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
        this.stepIndex = stepIndex;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public String getDescription() {
        return description;
    }
}


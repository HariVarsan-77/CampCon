package com.campusconnect.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Complaint {
    private String id;
    private String complaintId; // Unique formatted ID: e.g. CC-2026-0142
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String studentDepartment;
    private String studentRollNo;
    private String title;
    private String description;
    private Category category;
    private String location;
    private Priority priority;
    private ComplaintStatus status;
    private String imageUrl;
    private String imageFileName;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private String rejectionReason;

    private List<ManagementResponse> responses = new ArrayList<>();
    private List<InternalNote> internalNotes = new ArrayList<>();
    private List<ComplaintActivity> activities = new ArrayList<>();

    public Complaint() {
        this.id = UUID.randomUUID().toString();
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ComplaintStatus.SUBMITTED;
        this.priority = Priority.MEDIUM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public List<ManagementResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<ManagementResponse> responses) {
        this.responses = responses;
    }

    public List<InternalNote> getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(List<InternalNote> internalNotes) {
        this.internalNotes = internalNotes;
    }

    public List<ComplaintActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<ComplaintActivity> activities) {
        this.activities = activities;
    }

    public void addResponse(ManagementResponse response) {
        if (this.responses == null) {
            this.responses = new ArrayList<>();
        }
        this.responses.add(response);
        this.updatedAt = LocalDateTime.now();
    }

    public void addInternalNote(InternalNote note) {
        if (this.internalNotes == null) {
            this.internalNotes = new ArrayList<>();
        }
        this.internalNotes.add(note);
        this.updatedAt = LocalDateTime.now();
    }

    public void addActivity(ComplaintActivity activity) {
        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }
        this.activities.add(activity);
    }

    public boolean hasResponse() {
        return responses != null && !responses.isEmpty();
    }

    public ManagementResponse getLatestResponse() {
        if (hasResponse()) {
            return responses.get(responses.size() - 1);
        }
        return null;
    }

    public String getFormattedSubmittedAt() {
        if (submittedAt == null) return "";
        return submittedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"));
    }

    public String getFormattedDateOnly() {
        if (submittedAt == null) return "";
        return submittedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}


package com.campusconnect.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Notification {
    private String id;
    private String userId; // recipient
    private String title;
    private String message;
    private String link;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public Notification(String userId, String title, String message, String link) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.link = link;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"));
    }
}


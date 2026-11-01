package com.campusconnect.repository;

import com.campusconnect.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(String id);
    List<Notification> findByUserId(String userId);
    List<Notification> findUnreadByUserId(String userId);
    long countUnreadByUserId(String userId);
    void markAsRead(String id);
    void markAllAsRead(String userId);
}


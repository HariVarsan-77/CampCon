package com.campusconnect.service;

import com.campusconnect.model.Notification;
import com.campusconnect.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification notifyUser(String userId, String title, String message, String link) {
        Notification notification = new Notification(userId, title, message, link);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsForUser(String userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void markAsRead(String id) {
        notificationRepository.markAsRead(id);
    }

    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
    }
}


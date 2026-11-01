package com.campusconnect.repository;

import com.campusconnect.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {

    private final Map<String, Notification> notificationMap = new ConcurrentHashMap<>();

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null || notification.getId().isBlank()) {
            notification.setId(UUID.randomUUID().toString());
        }
        notificationMap.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public Optional<Notification> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(notificationMap.get(id));
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        return notificationMap.values().stream()
                .filter(n -> userId.equals(n.getUserId()))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        return notificationMap.values().stream()
                .filter(n -> userId.equals(n.getUserId()) && !n.isRead())
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadByUserId(String userId) {
        if (userId == null) return 0;
        return notificationMap.values().stream()
                .filter(n -> userId.equals(n.getUserId()) && !n.isRead())
                .count();
    }

    @Override
    public void markAsRead(String id) {
        if (id != null && notificationMap.containsKey(id)) {
            notificationMap.get(id).setRead(true);
        }
    }

    @Override
    public void markAllAsRead(String userId) {
        if (userId != null) {
            notificationMap.values().stream()
                    .filter(n -> userId.equals(n.getUserId()))
                    .forEach(n -> n.setRead(true));
        }
    }
}


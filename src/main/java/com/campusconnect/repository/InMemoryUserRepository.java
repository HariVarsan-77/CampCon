package com.campusconnect.repository;

import com.campusconnect.model.Role;
import com.campusconnect.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(java.util.UUID.randomUUID().toString());
        }
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userMap.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return userMap.values().stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public List<User> findByRole(Role role) {
        return userMap.values().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        if (id != null) {
            userMap.remove(id);
        }
    }

    @Override
    public long count() {
        return userMap.size();
    }
}


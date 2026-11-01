package com.campusconnect.repository;

import com.campusconnect.model.Role;
import com.campusconnect.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
    List<User> findByRole(Role role);
    void deleteById(String id);
    long count();
}


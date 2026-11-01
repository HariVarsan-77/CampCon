package com.campusconnect.service;

import com.campusconnect.dto.PasswordChangeRequest;
import com.campusconnect.dto.SignupRequest;
import com.campusconnect.dto.UserProfileUpdateRequest;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.model.Role;
import com.campusconnect.model.User;
import com.campusconnect.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT);
        user.setDepartment(request.getDepartment() != null ? request.getDepartment().trim() : "General");
        user.setIdentifier(request.getIdentifier() != null ? request.getIdentifier().trim() : "");
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User updateProfile(String userId, UserProfileUpdateRequest request) {
        User user = getById(userId);
        user.setFullName(request.getFullName().trim());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment().trim());
        if (request.getIdentifier() != null) user.setIdentifier(request.getIdentifier().trim());
        if (request.getPhone() != null) user.setPhone(request.getPhone().trim());
        return userRepository.save(user);
    }

    public void changePassword(String userId, PasswordChangeRequest request) {
        User user = getById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public long count() {
        return userRepository.count();
    }
}


package com.campusconnect.controller;

import com.campusconnect.model.Role;
import com.campusconnect.model.User;
import com.campusconnect.security.SecurityUtils;
import com.campusconnect.service.ComplaintService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    private final ComplaintService complaintService;

    public HomeController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        Optional<User> currentUser = SecurityUtils.getCurrentUser();
        currentUser.ifPresent(user -> {
            model.addAttribute("currentUser", user);
            model.addAttribute("isStudent", user.getRole() == Role.ROLE_STUDENT);
            model.addAttribute("isManagement", user.getRole() == Role.ROLE_MANAGEMENT);
        });

        // Add public stats for social proof
        var stats = complaintService.getManagementStats();
        model.addAttribute("totalComplaints", stats.get("total"));
        model.addAttribute("resolvedComplaints", stats.get("resolved"));

        return "home";
    }
}


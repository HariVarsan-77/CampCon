package com.campusconnect.controller;

import com.campusconnect.dto.SignupRequest;
import com.campusconnect.model.Role;
import com.campusconnect.model.User;
import com.campusconnect.security.SecurityUtils;
import com.campusconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "signupSuccess", required = false) String signupSuccess,
                            @RequestParam(value = "role", required = false, defaultValue = "student") String roleTab,
                            Model model) {
        if (SecurityUtils.isAuthenticated()) {
            if (SecurityUtils.hasRole(Role.ROLE_MANAGEMENT)) {
                return "redirect:/management/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please verify your credentials.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (signupSuccess != null) {
            model.addAttribute("successMessage", "Account created successfully! You can now log in.");
        }

        model.addAttribute("activeRoleTab", roleTab);
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(@RequestParam(value = "role", required = false, defaultValue = "student") String role,
                             Model model) {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/home";
        }

        SignupRequest signupRequest = new SignupRequest();
        if ("management".equalsIgnoreCase(role)) {
            signupRequest.setRole(Role.ROLE_MANAGEMENT);
        } else {
            signupRequest.setRole(Role.ROLE_STUDENT);
        }

        model.addAttribute("signupRequest", signupRequest);
        model.addAttribute("roles", Role.values());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "auth/signup";
        }

        try {
            userService.registerUser(signupRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account registered successfully! Please log in with your credentials.");
            String roleParam = signupRequest.getRole() == Role.ROLE_MANAGEMENT ? "management" : "student";
            return "redirect:/login?role=" + roleParam + "&signupSuccess=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/signup";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage",
                "If an account exists with " + email + ", a password reset link has been dispatched to your inbox. (Prototype simulation)");
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", SecurityUtils.getCurrentUser().orElse(null));
        return "auth/access-denied";
    }
}


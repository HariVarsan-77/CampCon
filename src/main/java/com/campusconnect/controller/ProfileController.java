package com.campusconnect.controller;

import com.campusconnect.dto.PasswordChangeRequest;
import com.campusconnect.dto.UserProfileUpdateRequest;
import com.campusconnect.exception.UnauthorizedException;
import com.campusconnect.model.User;
import com.campusconnect.security.SecurityUtils;
import com.campusconnect.service.NotificationService;
import com.campusconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final NotificationService notificationService;

    public ProfileController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    private User getAuthenticatedUser() {
        return SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new UnauthorizedException("Please log in to view profile."));
    }

    @GetMapping
    public String profilePage(Model model) {
        User user = getAuthenticatedUser();
        UserProfileUpdateRequest profileRequest = new UserProfileUpdateRequest();
        profileRequest.setFullName(user.getFullName());
        profileRequest.setDepartment(user.getDepartment());
        profileRequest.setIdentifier(user.getIdentifier());
        profileRequest.setPhone(user.getPhone());

        model.addAttribute("currentUser", user);
        model.addAttribute("profileRequest", profileRequest);
        model.addAttribute("passwordRequest", new PasswordChangeRequest());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user.getId()));
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileRequest") UserProfileUpdateRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid profile information.");
            return "redirect:/profile";
        }

        userService.updateProfile(user.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordRequest") PasswordChangeRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters.");
            return "redirect:/profile";
        }

        try {
            userService.changePassword(user.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/profile";
    }
}


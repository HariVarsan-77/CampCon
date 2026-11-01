package com.campusconnect.controller;

import com.campusconnect.exception.UnauthorizedException;
import com.campusconnect.model.Notification;
import com.campusconnect.model.User;
import com.campusconnect.security.SecurityUtils;
import com.campusconnect.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private User getAuthenticatedUser() {
        return SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new UnauthorizedException("Please log in."));
    }

    @GetMapping
    public String notificationsPage(Model model) {
        User user = getAuthenticatedUser();
        List<Notification> list = notificationService.getNotificationsForUser(user.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("notifications", list);
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(user.getId()));
        return "notifications";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable("id") String id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllRead(RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser();
        notificationService.markAllAsRead(user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/notifications";
    }
}


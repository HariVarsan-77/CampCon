package com.campusconnect.controller;

import com.campusconnect.dto.InternalNoteRequest;
import com.campusconnect.dto.ManagementReplyRequest;
import com.campusconnect.dto.RejectionRequest;
import com.campusconnect.dto.StatusUpdateRequest;
import com.campusconnect.exception.UnauthorizedException;
import com.campusconnect.model.*;
import com.campusconnect.security.SecurityUtils;
import com.campusconnect.service.ComplaintService;
import com.campusconnect.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management")
public class ManagementController {

    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    public ManagementController(ComplaintService complaintService, NotificationService notificationService) {
        this.complaintService = complaintService;
        this.notificationService = notificationService;
    }

    private User getAuthenticatedManagement() {
        return SecurityUtils.getCurrentUser()
                .filter(u -> u.getRole() == Role.ROLE_MANAGEMENT)
                .orElseThrow(() -> new UnauthorizedException("Access restricted to college management officers only."));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User management = getAuthenticatedManagement();
        model.addAttribute("currentUser", management);
        model.addAttribute("stats", complaintService.getManagementStats());

        // Get 6 most recent complaints
        List<Complaint> allComplaints = complaintService.getAllComplaints();
        List<Complaint> recentComplaints = allComplaints.size() > 6 ? allComplaints.subList(0, 6) : allComplaints;
        model.addAttribute("recentComplaints", recentComplaints);

        // Get high priority pending complaints count
        long criticalCount = allComplaints.stream()
                .filter(c -> (c.getStatus() == ComplaintStatus.SUBMITTED || c.getStatus() == ComplaintStatus.UNDER_REVIEW) &&
                        (c.getPriority() == Priority.CRITICAL || c.getPriority() == Priority.HIGH))
                .count();
        model.addAttribute("urgentPendingCount", criticalCount);
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(management.getId()));

        return "management/dashboard";
    }

    @GetMapping("/complaints")
    public String complaintList(@RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "category", required = false) String categoryName,
                                @RequestParam(value = "status", required = false) String statusName,
                                @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
                                Model model) {
        User management = getAuthenticatedManagement();
        model.addAttribute("currentUser", management);

        Category category = null;
        if (categoryName != null && !categoryName.isBlank()) {
            try { category = Category.valueOf(categoryName); } catch (Exception ignored) {}
        }

        ComplaintStatus status = null;
        if (statusName != null && !statusName.isBlank()) {
            try { status = ComplaintStatus.valueOf(statusName); } catch (Exception ignored) {}
        }

        List<Complaint> complaints = complaintService.filterComplaints(search, category, status, sort);

        model.addAttribute("complaints", complaints);
        model.addAttribute("categories", Category.values());
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("selectedCategory", categoryName);
        model.addAttribute("selectedStatus", statusName);
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("stats", complaintService.getManagementStats());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(management.getId()));

        return "management/complaints";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable("id") String id, Model model) {
        User management = getAuthenticatedManagement();
        Complaint complaint = complaintService.getById(id);

        model.addAttribute("currentUser", management);
        model.addAttribute("complaint", complaint);
        model.addAttribute("replyRequest", new ManagementReplyRequest());
        model.addAttribute("statusRequest", new StatusUpdateRequest());
        model.addAttribute("rejectionRequest", new RejectionRequest());
        model.addAttribute("noteRequest", new InternalNoteRequest());
        model.addAttribute("availableStatuses", ComplaintStatus.values());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(management.getId()));

        return "management/complaint-details";
    }

    @PostMapping("/complaints/{id}/reply")
    public String sendReply(@PathVariable("id") String id,
                            @Valid @ModelAttribute("replyRequest") ManagementReplyRequest request,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        User management = getAuthenticatedManagement();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reply text cannot be blank.");
            return "redirect:/management/complaints/" + id;
        }

        complaintService.addManagementReply(id, management, request.getMessage());
        redirectAttributes.addFlashAttribute("successMessage", "Official response dispatched to student successfully.");
        return "redirect:/management/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/status")
    public String updateStatus(@PathVariable("id") String id,
                               @Valid @ModelAttribute("statusRequest") StatusUpdateRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        User management = getAuthenticatedManagement();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid status selection.");
            return "redirect:/management/complaints/" + id;
        }

        complaintService.updateStatus(id, management, request.getStatus(), request.getComment());
        redirectAttributes.addFlashAttribute("successMessage",
                "Complaint status updated to " + request.getStatus().getDisplayName() + ".");
        return "redirect:/management/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/reject")
    public String rejectComplaint(@PathVariable("id") String id,
                                  @Valid @ModelAttribute("rejectionRequest") RejectionRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        User management = getAuthenticatedManagement();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "A rejection reason of at least 10 characters is required.");
            return "redirect:/management/complaints/" + id;
        }

        complaintService.rejectComplaint(id, management, request.getReason());
        redirectAttributes.addFlashAttribute("successMessage", "Complaint has been marked as Rejected.");
        return "redirect:/management/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/note")
    public String addInternalNote(@PathVariable("id") String id,
                                  @Valid @ModelAttribute("noteRequest") InternalNoteRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        User management = getAuthenticatedManagement();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Internal note cannot be empty.");
            return "redirect:/management/complaints/" + id;
        }

        complaintService.addInternalNote(id, management, request.getNote());
        redirectAttributes.addFlashAttribute("successMessage", "Internal staff note recorded (confidential to management).");
        return "redirect:/management/complaints/" + id;
    }
}


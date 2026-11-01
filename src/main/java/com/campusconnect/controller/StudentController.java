package com.campusconnect.controller;

import com.campusconnect.dto.ComplaintCreateRequest;
import com.campusconnect.exception.ResourceNotFoundException;
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
@RequestMapping("/student")
public class StudentController {

    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    public StudentController(ComplaintService complaintService, NotificationService notificationService) {
        this.complaintService = complaintService;
        this.notificationService = notificationService;
    }

    private User getAuthenticatedStudent() {
        return SecurityUtils.getCurrentUser()
                .filter(u -> u.getRole() == Role.ROLE_STUDENT)
                .orElseThrow(() -> new UnauthorizedException("Access restricted to authenticated students only."));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User student = getAuthenticatedStudent();
        model.addAttribute("currentUser", student);
        model.addAttribute("stats", complaintService.getStudentStats(student.getId()));
        model.addAttribute("categories", Category.values());

        // Get recent complaints (up to 5)
        List<Complaint> myComplaints = complaintService.getComplaintsForStudent(student.getId());
        List<Complaint> recentComplaints = myComplaints.size() > 5 ? myComplaints.subList(0, 5) : myComplaints;
        model.addAttribute("recentComplaints", recentComplaints);
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getId()));

        return "student/dashboard";
    }

    @GetMapping("/complaints")
    public String myComplaints(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "category", required = false) String categoryName,
                               @RequestParam(value = "status", required = false) String statusName,
                               @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
                               Model model) {
        User student = getAuthenticatedStudent();
        model.addAttribute("currentUser", student);

        Category category = null;
        if (categoryName != null && !categoryName.isBlank()) {
            try { category = Category.valueOf(categoryName); } catch (Exception ignored) {}
        }

        ComplaintStatus status = null;
        if (statusName != null && !statusName.isBlank()) {
            try { status = ComplaintStatus.valueOf(statusName); } catch (Exception ignored) {}
        }

        List<Complaint> complaints = complaintService.filterStudentComplaints(
                student.getId(), search, category, status, sort
        );

        model.addAttribute("complaints", complaints);
        model.addAttribute("categories", Category.values());
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("selectedCategory", categoryName);
        model.addAttribute("selectedStatus", statusName);
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("stats", complaintService.getStudentStats(student.getId()));
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getId()));

        return "student/my-complaints";
    }

    @GetMapping("/complaints/new")
    public String newComplaintForm(@RequestParam(value = "category", required = false) String categoryName,
                                   Model model) {
        User student = getAuthenticatedStudent();
        model.addAttribute("currentUser", student);

        ComplaintCreateRequest request = new ComplaintCreateRequest();
        if (categoryName != null && !categoryName.isBlank()) {
            try {
                request.setCategory(Category.valueOf(categoryName.toUpperCase()));
            } catch (Exception ignored) {}
        }

        model.addAttribute("complaintRequest", request);
        model.addAttribute("categories", Category.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getId()));
        return "student/file-complaint";
    }

    @PostMapping("/complaints/new")
    public String submitComplaint(@Valid @ModelAttribute("complaintRequest") ComplaintCreateRequest request,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User student = getAuthenticatedStudent();

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", student);
            model.addAttribute("categories", Category.values());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getId()));
            return "student/file-complaint";
        }

        try {
            Complaint saved = complaintService.createComplaint(student, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Grievance submitted successfully! Reference ID: " + saved.getComplaintId());
            return "redirect:/student/complaints/" + saved.getId();
        } catch (Exception ex) {
            model.addAttribute("currentUser", student);
            model.addAttribute("categories", Category.values());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("errorMessage", "Submission failed: " + ex.getMessage());
            model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getId()));
            return "student/file-complaint";
        }
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable("id") String id, Model model) {
        User student = getAuthenticatedStudent();
        Complaint complaint = complaintService.getById(id);

        // Security check: Student can only view their own complaints
        if (!student.getId().equals(complaint.getStudentId())) {
            throw new UnauthorizedException("You are not authorized to view complaints filed by other students.");
        }

        model.addAttribute("currentUser", student);
        model.addAttribute("complaint", complaint);
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(student.getId()));
        return "student/complaint-details";
    }
}


package com.campusconnect.service;

import com.campusconnect.dto.ComplaintCreateRequest;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.model.*;
import com.campusconnect.repository.ComplaintRepository;
import com.campusconnect.service.storage.FileStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public ComplaintService(ComplaintRepository complaintRepository,
                            FileStorageService fileStorageService,
                            NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    public Complaint createComplaint(User student, ComplaintCreateRequest request) {
        Complaint complaint = new Complaint();
        complaint.setStudentId(student.getId());
        complaint.setStudentName(student.getFullName());
        complaint.setStudentEmail(student.getEmail());
        complaint.setStudentDepartment(student.getDepartment());
        complaint.setStudentRollNo(student.getIdentifier());

        complaint.setTitle(request.getTitle().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setCategory(request.getCategory());
        complaint.setLocation(request.getLocation().trim());
        complaint.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        // Handle image upload if provided
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String storedPath = fileStorageService.storeFile(request.getImageFile());
            complaint.setImageUrl(storedPath);
            complaint.setImageFileName(request.getImageFile().getOriginalFilename());
        }

        // Add initial activity
        complaint.addActivity(new ComplaintActivity(
                student.getFullName(),
                "Complaint Submitted",
                "Grievance lodged under category '" + request.getCategory().getDisplayName() +
                        "' with priority " + complaint.getPriority().getDisplayName()
        ));

        Complaint saved = complaintRepository.save(complaint);

        // Notify student of successful registration
        notificationService.notifyUser(
                student.getId(),
                "Complaint Filed",
                "Your complaint #" + saved.getComplaintId() + " has been registered successfully.",
                "/student/complaints/" + saved.getId()
        );

        return saved;
    }

    public Complaint getById(String id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
    }

    public Complaint getByComplaintId(String complaintId) {
        return complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with number: " + complaintId));
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsForStudent(String studentId) {
        return complaintRepository.findByStudentId(studentId);
    }

    public List<Complaint> filterComplaints(String query, Category category, ComplaintStatus status, String sort) {
        return complaintRepository.searchAndFilter(query, category, status, sort);
    }

    public List<Complaint> filterStudentComplaints(String studentId, String query, Category category, ComplaintStatus status, String sort) {
        return complaintRepository.searchAndFilterForStudent(studentId, query, category, status, sort);
    }

    public Complaint addManagementReply(String complaintId, User managementUser, String replyText) {
        Complaint complaint = getById(complaintId);

        ManagementResponse response = new ManagementResponse(
                managementUser.getFullName(),
                managementUser.getEmail(),
                managementUser.getDepartment() != null ? managementUser.getDepartment() : "Administration",
                replyText.trim()
        );

        complaint.addResponse(response);

        // Log activity
        complaint.addActivity(new ComplaintActivity(
                managementUser.getFullName(),
                "Response Sent",
                "Management responded: \"" + truncate(replyText.trim(), 80) + "\""
        ));

        Complaint updated = complaintRepository.save(complaint);

        // Notify student
        notificationService.notifyUser(
                complaint.getStudentId(),
                "New Management Response",
                "Management has replied to your complaint #" + complaint.getComplaintId() + ".",
                "/student/complaints/" + complaint.getId()
        );

        return updated;
    }

    public Complaint updateStatus(String complaintId, User managementUser, ComplaintStatus newStatus, String comment) {
        Complaint complaint = getById(complaintId);
        ComplaintStatus oldStatus = complaint.getStatus();

        if (oldStatus == newStatus) {
            return complaint;
        }

        complaint.setStatus(newStatus);

        String detail = "Status changed from " + oldStatus.getDisplayName() + " to " + newStatus.getDisplayName();
        if (comment != null && !comment.trim().isEmpty()) {
            detail += ". Note: " + comment.trim();
        }

        complaint.addActivity(new ComplaintActivity(
                managementUser.getFullName(),
                "Status Updated to " + newStatus.getDisplayName(),
                detail
        ));

        Complaint updated = complaintRepository.save(complaint);

        // Notify student
        notificationService.notifyUser(
                complaint.getStudentId(),
                "Complaint Status Updated",
                "Complaint #" + complaint.getComplaintId() + " is now " + newStatus.getDisplayName() + ".",
                "/student/complaints/" + complaint.getId()
        );

        return updated;
    }

    public Complaint rejectComplaint(String complaintId, User managementUser, String reason) {
        Complaint complaint = getById(complaintId);

        complaint.setStatus(ComplaintStatus.REJECTED);
        complaint.setRejectionReason(reason.trim());

        complaint.addActivity(new ComplaintActivity(
                managementUser.getFullName(),
                "Complaint Rejected",
                "Reason: " + reason.trim()
        ));

        Complaint updated = complaintRepository.save(complaint);

        // Notify student
        notificationService.notifyUser(
                complaint.getStudentId(),
                "Complaint Rejected",
                "Your complaint #" + complaint.getComplaintId() + " was rejected. Reason: " + truncate(reason.trim(), 60),
                "/student/complaints/" + complaint.getId()
        );

        return updated;
    }

    public Complaint addInternalNote(String complaintId, User managementUser, String noteText) {
        Complaint complaint = getById(complaintId);

        InternalNote note = new InternalNote(
                managementUser.getFullName(),
                managementUser.getEmail(),
                noteText.trim()
        );

        complaint.addInternalNote(note);

        complaint.addActivity(new ComplaintActivity(
                managementUser.getFullName(),
                "Internal Note Added",
                "Internal staff note added: \"" + truncate(noteText.trim(), 80) + "\""
        ));

        return complaintRepository.save(complaint);
    }

    public Map<String, Long> getStudentStats(String studentId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", complaintRepository.countByStudentId(studentId));
        stats.put("pending",
                complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.SUBMITTED) +
                complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.UNDER_REVIEW) +
                complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.ACTION_REQUIRED)
        );
        stats.put("resolved", complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.RESOLVED));
        stats.put("rejected", complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.REJECTED));
        return stats;
    }

    public Map<String, Long> getManagementStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", complaintRepository.count());
        stats.put("submitted", complaintRepository.countByStatus(ComplaintStatus.SUBMITTED));
        stats.put("underReview", complaintRepository.countByStatus(ComplaintStatus.UNDER_REVIEW));
        stats.put("actionRequired", complaintRepository.countByStatus(ComplaintStatus.ACTION_REQUIRED));
        stats.put("resolved", complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        stats.put("rejected", complaintRepository.countByStatus(ComplaintStatus.REJECTED));
        return stats;
    }

    public Complaint saveDirectly(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}


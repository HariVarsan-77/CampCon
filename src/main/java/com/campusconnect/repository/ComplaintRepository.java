package com.campusconnect.repository;

import com.campusconnect.model.Category;
import com.campusconnect.model.Complaint;
import com.campusconnect.model.ComplaintStatus;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository {
    Complaint save(Complaint complaint);
    Optional<Complaint> findById(String id);
    Optional<Complaint> findByComplaintId(String complaintId);
    List<Complaint> findAll();
    List<Complaint> findByStudentId(String studentId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByCategory(Category category);
    List<Complaint> searchAndFilter(String query, Category category, ComplaintStatus status, String sort);
    List<Complaint> searchAndFilterForStudent(String studentId, String query, Category category, ComplaintStatus status, String sort);
    long count();
    long countByStudentId(String studentId);
    long countByStatus(ComplaintStatus status);
    long countByStudentIdAndStatus(String studentId, ComplaintStatus status);
    void deleteById(String id);
    String generateNextComplaintId();
}


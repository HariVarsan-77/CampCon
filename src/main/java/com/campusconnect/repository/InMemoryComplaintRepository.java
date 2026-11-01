package com.campusconnect.repository;

import com.campusconnect.model.Category;
import com.campusconnect.model.Complaint;
import com.campusconnect.model.ComplaintStatus;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryComplaintRepository implements ComplaintRepository {

    private final Map<String, Complaint> complaintMap = new ConcurrentHashMap<>();
    private final AtomicInteger sequenceCounter = new AtomicInteger(1000);

    @Override
    public Complaint save(Complaint complaint) {
        if (complaint.getId() == null || complaint.getId().isBlank()) {
            complaint.setId(UUID.randomUUID().toString());
        }
        if (complaint.getComplaintId() == null || complaint.getComplaintId().isBlank()) {
            complaint.setComplaintId(generateNextComplaintId());
        }
        complaintMap.put(complaint.getId(), complaint);
        return complaint;
    }

    @Override
    public Optional<Complaint> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(complaintMap.get(id));
    }

    @Override
    public Optional<Complaint> findByComplaintId(String complaintId) {
        if (complaintId == null) return Optional.empty();
        return complaintMap.values().stream()
                .filter(c -> c.getComplaintId() != null && c.getComplaintId().equalsIgnoreCase(complaintId.trim()))
                .findFirst();
    }

    @Override
    public List<Complaint> findAll() {
        return complaintMap.values().stream()
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStudentId(String studentId) {
        if (studentId == null) return Collections.emptyList();
        return complaintMap.values().stream()
                .filter(c -> studentId.equals(c.getStudentId()))
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return complaintMap.values().stream()
                .filter(c -> c.getStatus() == status)
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByCategory(Category category) {
        return complaintMap.values().stream()
                .filter(c -> c.getCategory() == category)
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> searchAndFilter(String query, Category category, ComplaintStatus status, String sort) {
        return filterStream(complaintMap.values().stream(), query, category, status, sort);
    }

    @Override
    public List<Complaint> searchAndFilterForStudent(String studentId, String query, Category category, ComplaintStatus status, String sort) {
        if (studentId == null) return Collections.emptyList();
        return filterStream(complaintMap.values().stream().filter(c -> studentId.equals(c.getStudentId())),
                query, category, status, sort);
    }

    private List<Complaint> filterStream(java.util.stream.Stream<Complaint> stream,
                                         String query, Category category, ComplaintStatus status, String sort) {
        if (query != null && !query.trim().isEmpty()) {
            final String q = query.trim().toLowerCase();
            stream = stream.filter(c ->
                    (c.getComplaintId() != null && c.getComplaintId().toLowerCase().contains(q)) ||
                    (c.getTitle() != null && c.getTitle().toLowerCase().contains(q)) ||
                    (c.getStudentName() != null && c.getStudentName().toLowerCase().contains(q)) ||
                    (c.getLocation() != null && c.getLocation().toLowerCase().contains(q)) ||
                    (c.getDescription() != null && c.getDescription().toLowerCase().contains(q))
            );
        }

        if (category != null) {
            stream = stream.filter(c -> c.getCategory() == category);
        }

        if (status != null) {
            stream = stream.filter(c -> c.getStatus() == status);
        }

        Comparator<Complaint> comparator = Comparator.comparing(Complaint::getSubmittedAt).reversed();
        if ("oldest".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(Complaint::getSubmittedAt);
        } else if ("priority".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(Complaint::getPriority).reversed()
                    .thenComparing(Comparator.comparing(Complaint::getSubmittedAt).reversed());
        }

        return stream.sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return complaintMap.size();
    }

    @Override
    public long countByStudentId(String studentId) {
        if (studentId == null) return 0;
        return complaintMap.values().stream().filter(c -> studentId.equals(c.getStudentId())).count();
    }

    @Override
    public long countByStatus(ComplaintStatus status) {
        return complaintMap.values().stream().filter(c -> c.getStatus() == status).count();
    }

    @Override
    public long countByStudentIdAndStatus(String studentId, ComplaintStatus status) {
        if (studentId == null) return 0;
        return complaintMap.values().stream()
                .filter(c -> studentId.equals(c.getStudentId()) && c.getStatus() == status)
                .count();
    }

    @Override
    public void deleteById(String id) {
        if (id != null) {
            complaintMap.remove(id);
        }
    }

    @Override
    public synchronized String generateNextComplaintId() {
        int seq = sequenceCounter.incrementAndGet();
        int currentYear = Year.now().getValue();
        return String.format("CC-%d-%04d", currentYear, seq);
    }
}


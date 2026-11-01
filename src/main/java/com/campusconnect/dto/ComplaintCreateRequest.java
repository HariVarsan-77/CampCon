package com.campusconnect.dto;

import com.campusconnect.model.Category;
import com.campusconnect.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ComplaintCreateRequest {

    @NotBlank(message = "Complaint title is required")
    @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 15, max = 2500, message = "Description must provide at least 15 characters of detail")
    private String description;

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Location is required (e.g. Block A, Room 304, Main Canteen)")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;

    private Priority priority = Priority.MEDIUM;

    private MultipartFile imageFile;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}


package com.campusconnect.service.storage;

import com.campusconnect.exception.StorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadLocation;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp"
    );

    public LocalFileStorageService(@Value("${campusconnect.storage.upload-dir:uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage directory at " + this.uploadLocation, e);
        }
    }

    @Override
    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new StorageException("Uploaded file must have a valid filename");
        }

        String extension = getExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new StorageException("Unsupported image format (" + extension + "). Please upload JPG, JPEG, PNG, or WEBP.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new StorageException("Invalid image content type: " + contentType);
        }

        // Limit to 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new StorageException("Image file size exceeds maximum limit of 10MB");
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file);

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg"
        );
        String extension = getExtension(originalFilename);
        String safeName = UUID.randomUUID().toString() + extension;

        try {
            if (safeName.contains("..")) {
                throw new StorageException("Invalid path sequence in filename " + safeName);
            }

            Path targetPath = this.uploadLocation.resolve(safeName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return the web-accessible URL path
            return "/uploads/" + safeName;
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + safeName, e);
        }
    }

    @Override
    public Resource loadAsResource(String fileName) {
        try {
            Path filePath = this.uploadLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("File path invalid for: " + fileName, e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.uploadLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Path getUploadPath() {
        return this.uploadLocation;
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            return filename.substring(dot);
        }
        return "";
    }
}


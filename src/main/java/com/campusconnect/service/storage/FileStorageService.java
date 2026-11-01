package com.campusconnect.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Storage abstraction for complaint image evidence and user uploads.
 * Can be swapped with FirebaseStorageService or SupabaseStorageService in future.
 */
public interface FileStorageService {
    /**
     * Store an uploaded image and return its public URL / path reference
     */
    String storeFile(MultipartFile file);

    /**
     * Load a file as a Resource
     */
    Resource loadAsResource(String fileName);

    /**
     * Delete a stored file
     */
    boolean deleteFile(String fileName);

    /**
     * Get root path
     */
    Path getUploadPath();

    /**
     * Validate file type and size
     */
    void validateImage(MultipartFile file);
}


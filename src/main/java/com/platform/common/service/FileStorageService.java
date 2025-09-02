package com.platform.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface FileStorageService {
    
    /**
     * Upload a file and return the URL
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * Upload a file with custom filename
     */
    String uploadFile(MultipartFile file, String directory, String filename) throws IOException;
    
    /**
     * Delete a file by URL
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * Get file URL by filename
     */
    String getFileUrl(String filename, String directory);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String fileUrl);
    
    /**
     * Get file size in bytes
     */
    long getFileSize(String fileUrl);
    
    /**
     * Validate file type
     */
    boolean isValidFileType(MultipartFile file);
    
    /**
     * Validate file size
     */
    boolean isValidFileSize(MultipartFile file);
    
    /**
     * Generate unique filename
     */
    String generateUniqueFilename(String originalFilename);
    
    /**
     * Get storage type (S3 or LOCAL)
     */
    String getStorageType();
}

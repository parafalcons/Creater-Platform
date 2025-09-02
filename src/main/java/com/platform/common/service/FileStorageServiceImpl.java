package com.platform.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    private final GlobalConfigurationService configService;
    
    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        return uploadFile(file, directory, generateUniqueFilename(file.getOriginalFilename()));
    }
    
    @Override
    public String uploadFile(MultipartFile file, String directory, String filename) throws IOException {
        // Validate file
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: " + configService.getAllowedFileTypes());
        }
        
        if (!isValidFileSize(file)) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + configService.getMaxFileSizeMB() + "MB");
        }
        
        if (configService.isS3Enabled()) {
            return uploadToS3(file, directory, filename);
        } else {
            return uploadToLocal(file, directory, filename);
        }
    }
    
    private String uploadToS3(MultipartFile file, String directory, String filename) throws IOException {
        // TODO: Implement S3 upload logic
        // For now, return a simulated S3 URL
        String s3Url = String.format("https://s3.amazonaws.com/%s/%s/%s", 
                configService.getS3BucketName(), directory, filename);
        
        log.info("File uploaded to S3: {}", s3Url);
        return s3Url;
    }
    
    private String uploadToLocal(MultipartFile file, String directory, String filename) throws IOException {
        String basePath = configService.getLocalStoragePath();
        Path uploadPath = Paths.get(basePath, directory);
        
        // Create directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        // Return local file URL
        String localUrl = String.format("/uploads/%s/%s", directory, filename);
        log.info("File uploaded to local storage: {}", localUrl);
        return localUrl;
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        if (configService.isS3Enabled()) {
            return deleteFromS3(fileUrl);
        } else {
            return deleteFromLocal(fileUrl);
        }
    }
    
    private boolean deleteFromS3(String fileUrl) {
        // TODO: Implement S3 delete logic
        log.info("File deleted from S3: {}", fileUrl);
        return true;
    }
    
    private boolean deleteFromLocal(String fileUrl) {
        try {
            String basePath = configService.getLocalStoragePath();
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(basePath, relativePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted from local storage: {}", fileUrl);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Error deleting file from local storage: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String filename, String directory) {
        if (configService.isS3Enabled()) {
            return String.format("https://s3.amazonaws.com/%s/%s/%s", 
                    configService.getS3BucketName(), directory, filename);
        } else {
            return String.format("/uploads/%s/%s", directory, filename);
        }
    }
    
    @Override
    public boolean fileExists(String fileUrl) {
        if (configService.isS3Enabled()) {
            // TODO: Implement S3 file existence check
            return true;
        } else {
            try {
                String basePath = configService.getLocalStoragePath();
                String relativePath = fileUrl.replace("/uploads/", "");
                Path filePath = Paths.get(basePath, relativePath);
                return Files.exists(filePath);
            } catch (Exception e) {
                log.error("Error checking file existence: {}", fileUrl, e);
                return false;
            }
        }
    }
    
    @Override
    public long getFileSize(String fileUrl) {
        if (configService.isS3Enabled()) {
            // TODO: Implement S3 file size check
            return 0;
        } else {
            try {
                String basePath = configService.getLocalStoragePath();
                String relativePath = fileUrl.replace("/uploads/", "");
                Path filePath = Paths.get(basePath, relativePath);
                
                if (Files.exists(filePath)) {
                    return Files.size(filePath);
                }
                return 0;
            } catch (IOException e) {
                log.error("Error getting file size: {}", fileUrl, e);
                return 0;
            }
        }
    }
    
    @Override
    public boolean isValidFileType(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return false;
        }
        
        String originalFilename = file.getOriginalFilename().toLowerCase();
        String allowedTypes = configService.getAllowedFileTypes().toLowerCase();
        List<String> allowedExtensions = Arrays.asList(allowedTypes.split(","));
        
        for (String extension : allowedExtensions) {
            if (originalFilename.endsWith(extension.trim())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isValidFileSize(MultipartFile file) {
        if (file == null) {
            return false;
        }
        
        long maxSizeBytes = configService.getMaxFileSizeMB() * 1024 * 1024; // Convert MB to bytes
        return file.getSize() <= maxSizeBytes;
    }
    
    @Override
    public String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null) {
            originalFilename = "file";
        }
        
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
            originalFilename = originalFilename.substring(0, lastDotIndex);
        }
        
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        return originalFilename + "_" + uniqueId + extension;
    }
    
    @Override
    public String getStorageType() {
        return configService.isS3Enabled() ? "S3" : "LOCAL";
    }
}

package com.platform.common.service;

import com.platform.common.GlobalConfiguration;

import java.util.List;
import java.util.Optional;

public interface GlobalConfigurationService {
    
    /**
     * Get configuration value by key
     */
    Optional<String> getConfigValue(String configKey);
    
    /**
     * Get configuration value by key with default value
     */
    String getConfigValue(String configKey, String defaultValue);
    
    /**
     * Get boolean configuration value
     */
    boolean getBooleanConfig(String configKey, boolean defaultValue);
    
    /**
     * Get integer configuration value
     */
    int getIntConfig(String configKey, int defaultValue);
    
    /**
     * Get long configuration value
     */
    long getLongConfig(String configKey, long defaultValue);
    
    /**
     * Get double configuration value
     */
    double getDoubleConfig(String configKey, double defaultValue);
    
    /**
     * Set configuration value
     */
    GlobalConfiguration setConfigValue(String configKey, String configValue, String description);
    
    /**
     * Get all active configurations
     */
    List<GlobalConfiguration> getAllActiveConfigurations();
    
    /**
     * Check if S3 is enabled
     */
    boolean isS3Enabled();
    
    /**
     * Get S3 bucket name
     */
    String getS3BucketName();
    
    /**
     * Get S3 region
     */
    String getS3Region();
    
    /**
     * Get local storage path
     */
    String getLocalStoragePath();
    
    /**
     * Get maximum file size in MB
     */
    int getMaxFileSizeMB();
    
    /**
     * Get allowed file types
     */
    String getAllowedFileTypes();
    
    /**
     * Check if file upload is enabled
     */
    boolean isFileUploadEnabled();
    
    /**
     * Check if image compression is enabled
     */
    boolean isImageCompressionEnabled();
    
    /**
     * Get image compression quality
     */
    int getImageCompressionQuality();
}

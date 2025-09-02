package com.platform.common.service;

import com.platform.common.GlobalConfiguration;
import com.platform.common.repository.GlobalConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalConfigurationServiceImpl implements GlobalConfigurationService {
    
    private final GlobalConfigurationRepository globalConfigurationRepository;
    
    @Override
    @Cacheable(value = "config", key = "#configKey")
    public Optional<String> getConfigValue(String configKey) {
        return globalConfigurationRepository.findConfigValueByKey(configKey);
    }
    
    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        return getConfigValue(configKey).orElse(defaultValue);
    }
    
    @Override
    public boolean getBooleanConfig(String configKey, boolean defaultValue) {
        String value = getConfigValue(configKey, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    @Override
    public int getIntConfig(String configKey, int defaultValue) {
        String value = getConfigValue(configKey, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for config key {}: {}", configKey, value);
            return defaultValue;
        }
    }
    
    @Override
    public long getLongConfig(String configKey, long defaultValue) {
        String value = getConfigValue(configKey, String.valueOf(defaultValue));
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid long value for config key {}: {}", configKey, value);
            return defaultValue;
        }
    }
    
    @Override
    public double getDoubleConfig(String configKey, double defaultValue) {
        String value = getConfigValue(configKey, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid double value for config key {}: {}", configKey, value);
            return defaultValue;
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "config", key = "#configKey")
    public GlobalConfiguration setConfigValue(String configKey, String configValue, String description) {
        Optional<GlobalConfiguration> existingConfig = globalConfigurationRepository.findActiveConfigByKey(configKey);
        
        if (existingConfig.isPresent()) {
            GlobalConfiguration config = existingConfig.get();
            config.setConfigValue(configValue);
            config.setDescription(description);
            return globalConfigurationRepository.save(config);
        } else {
            GlobalConfiguration newConfig = GlobalConfiguration.builder()
                    .configKey(configKey)
                    .configValue(configValue)
                    .description(description)
                    .isActive(true)
                    .build();
            return globalConfigurationRepository.save(newConfig);
        }
    }
    
    @Override
    public List<GlobalConfiguration> getAllActiveConfigurations() {
        return globalConfigurationRepository.findByIsActiveTrue();
    }
    
    @Override
    public boolean isS3Enabled() {
        return getBooleanConfig("s3_enabled", false);
    }
    
    @Override
    public String getS3BucketName() {
        return getConfigValue("s3_bucket_name", "blog-platform-images");
    }
    
    @Override
    public String getS3Region() {
        return getConfigValue("s3_region", "us-east-1");
    }
    
    @Override
    public String getLocalStoragePath() {
        return getConfigValue("local_storage_path", "/app/uploads");
    }
    
    @Override
    public int getMaxFileSizeMB() {
        return getIntConfig("max_file_size_mb", 10);
    }
    
    @Override
    public String getAllowedFileTypes() {
        return getConfigValue("allowed_file_types", "jpg,jpeg,png,gif");
    }
    
    @Override
    public boolean isFileUploadEnabled() {
        return getBooleanConfig("file_upload_enabled", true);
    }
    
    @Override
    public boolean isImageCompressionEnabled() {
        return getBooleanConfig("image_compression_enabled", true);
    }
    
    @Override
    public int getImageCompressionQuality() {
        return getIntConfig("image_compression_quality", 80);
    }
}

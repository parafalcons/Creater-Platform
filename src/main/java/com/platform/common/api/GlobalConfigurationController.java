package com.platform.common.api;

import com.platform.common.GlobalConfiguration;
import com.platform.common.service.GlobalConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@Slf4j
public class GlobalConfigurationController {
    
    private final GlobalConfigurationService configService;
    
    @GetMapping
    public ResponseEntity<List<GlobalConfiguration>> getAllConfigurations() {
        List<GlobalConfiguration> configurations = configService.getAllActiveConfigurations();
        return ResponseEntity.ok(configurations);
    }
    
    @GetMapping("/{configKey}")
    public ResponseEntity<String> getConfigValue(@PathVariable String configKey) {
        return configService.getConfigValue(configKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{configKey}")
    public ResponseEntity<GlobalConfiguration> updateConfiguration(
            Authentication authentication,
            @PathVariable String configKey,
            @RequestBody Map<String, String> request) {
        
        String configValue = request.get("configValue");
        String description = request.get("description");
        
        if (configValue == null) {
            return ResponseEntity.badRequest().build();
        }
        
        GlobalConfiguration updatedConfig = configService.setConfigValue(configKey, configValue, description);
        log.info("Configuration updated by user {}: {} = {}", 
                authentication.getName(), configKey, configValue);
        
        return ResponseEntity.ok(updatedConfig);
    }
    
    @GetMapping("/storage/type")
    public ResponseEntity<Map<String, String>> getStorageType() {
        String storageType = configService.isS3Enabled() ? "S3" : "LOCAL";
        return ResponseEntity.ok(Map.of("storageType", storageType));
    }
    
    @GetMapping("/storage/s3-enabled")
    public ResponseEntity<Map<String, Boolean>> isS3Enabled() {
        return ResponseEntity.ok(Map.of("s3Enabled", configService.isS3Enabled()));
    }
    
    @GetMapping("/file-upload/enabled")
    public ResponseEntity<Map<String, Boolean>> isFileUploadEnabled() {
        return ResponseEntity.ok(Map.of("fileUploadEnabled", configService.isFileUploadEnabled()));
    }
    
    @GetMapping("/file-upload/max-size")
    public ResponseEntity<Map<String, Integer>> getMaxFileSize() {
        return ResponseEntity.ok(Map.of("maxFileSizeMB", configService.getMaxFileSizeMB()));
    }
    
    @GetMapping("/file-upload/allowed-types")
    public ResponseEntity<Map<String, String>> getAllowedFileTypes() {
        return ResponseEntity.ok(Map.of("allowedFileTypes", configService.getAllowedFileTypes()));
    }
}

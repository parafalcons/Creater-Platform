package com.platform.notification.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    
    private Long id;
    private Long senderId;
    private String senderUserName;
    private String senderFullName;
    private String senderProfilePhoto;
    private String type;
    private String message;
    private Long targetId;
    private String targetType;
    private boolean isRead;
    private String targetUrl;
    private LocalDateTime createdAt;
} 
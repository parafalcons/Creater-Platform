package com.platform.notification.service;

import java.util.List;

import com.platform.notification.response.NotificationResponse;

public interface NotificationService {
    
    void createNotification(Long recipientId, Long senderId, String type, 
                           String message, Long targetId, String targetType, String targetUrl);
    
    List<NotificationResponse> getUserNotifications(Long userId);
    
    List<NotificationResponse> getUnreadNotifications(Long userId);
    
    void markAsRead(Long notificationId, Long userId);
    
    void markAllAsRead(Long userId);
    
    Long getUnreadCount(Long userId);
} 
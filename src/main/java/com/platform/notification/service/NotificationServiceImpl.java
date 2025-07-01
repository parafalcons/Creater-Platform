package com.platform.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.notification.domain.Notification;
import com.platform.notification.repository.NotificationRepository;
import com.platform.notification.response.NotificationResponse;
import com.platform.user.domain.User;
import com.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void createNotification(Long recipientId, Long senderId, String type, 
                                  String message, Long targetId, String targetType, String targetUrl) {
        User sender = userService.getUserByUserId(senderId);
        
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .senderId(senderId)
                .senderUserName(sender.getUserName())
                .senderFullName(sender.getFullName())
                .senderProfilePhoto(null)
                .type(type)
                .message(message)
                .targetId(targetId)
                .targetType(targetType)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadOrderByCreatedAtDesc(userId, false);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getRecipientId().equals(userId)) {
            throw new RuntimeException("Unauthorized to modify this notification");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsRead(userId, false);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        BeanUtils.copyProperties(notification, response);
        return response;
    }
} 
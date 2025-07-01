package com.platform.notification.domain;

import com.platform.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {
    
    @Column(nullable = false)
    private Long recipientId; // User who receives the notification
    
    @Column(nullable = false)
    private Long senderId; // User who triggered the notification
    
    @Column(nullable = false)
    private String senderUserName;
    
    @Column(nullable = false)
    private String senderFullName;
    
    @Column(nullable = false)
    private String senderProfilePhoto;
    
    @Column(nullable = false)
    private String type; // "LIKE", "COMMENT", "FOLLOW", "MENTION"
    
    @Column(nullable = false)
    private String message; // Human readable message
    
    @Column(nullable = false)
    private Long targetId; // Post ID, Comment ID, or User ID depending on type
    
    @Column(nullable = false)
    private String targetType; // "POST", "COMMENT", "USER"
    
    @Column(nullable = false)
    private boolean isRead = false;
    
    @Column(nullable = false)
    private String targetUrl; // URL to navigate when notification is clicked
} 
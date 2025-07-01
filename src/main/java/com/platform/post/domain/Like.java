package com.platform.post.domain;

import com.platform.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "like_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like extends BaseEntity {
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String userName;
    
    @Column(nullable = false)
    private String userFullName;
    
    @Column(nullable = false)
    private String userProfilePhoto;
    
    @Column(nullable = false)
    private Long targetId; // Post ID or Comment ID
    
    @Column(nullable = false)
    private String targetType; // "POST" or "COMMENT"
} 
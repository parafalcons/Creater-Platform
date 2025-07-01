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
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String userName;
    
    @Column(nullable = false)
    private String userFullName;
    
    @Column(nullable = false)
    private String userProfilePhoto;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    private Long likesCount = 0L;
    
    @Column(nullable = false)
    private boolean isLikedByCurrentUser = false;
} 
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
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String userName;
    
    @Column(nullable = false)
    private String userFullName;
    
    @Column(nullable = false)
    private String userProfilePhoto;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String caption;
    
    @Column(nullable = false)
    private Long likesCount = 0L;
    
    @Column(nullable = false)
    private Long commentsCount = 0L;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private boolean isLikedByCurrentUser = false;
} 
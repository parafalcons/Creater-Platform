package com.platform.post.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    
    private Long id;
    private Long postId;
    private Long userId;
    private String userName;
    private String userFullName;
    private String userProfilePhoto;
    private String content;
    private Long likesCount;
    private boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
} 
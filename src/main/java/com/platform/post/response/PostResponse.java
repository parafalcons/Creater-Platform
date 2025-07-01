package com.platform.post.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userFullName;
    private String userProfilePhoto;
    private String imageUrl;
    private String caption;
    private Long likesCount;
    private Long commentsCount;
    private String location;
    private boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;
} 
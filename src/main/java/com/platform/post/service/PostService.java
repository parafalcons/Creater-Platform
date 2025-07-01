package com.platform.post.service;

import java.util.List;

import com.platform.post.request.CreatePostRequest;
import com.platform.post.response.PostResponse;

public interface PostService {
    
    PostResponse createPost(Long userId, CreatePostRequest request);
    
    PostResponse getPostById(Long postId, Long currentUserId);
    
    List<PostResponse> getUserPosts(Long userId, Long currentUserId);
    
    List<PostResponse> getFeedPosts(Long currentUserId, int page, int size);
    
    List<PostResponse> searchPosts(String searchTerm, Long currentUserId);
    
    boolean likePost(Long postId, Long userId);
    
    boolean unlikePost(Long postId, Long userId);
    
    boolean deletePost(Long postId, Long userId);
} 
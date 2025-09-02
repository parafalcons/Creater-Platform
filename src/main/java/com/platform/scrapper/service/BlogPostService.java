package com.platform.scrapper.service;

import com.platform.scrapper.request.CreateBlogPostRequest;
import com.platform.scrapper.response.BlogPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogPostService {
    
    List<BlogPostResponse> getAllBlogPosts();
    
    Page<BlogPostResponse> getBlogPostsPaginated(Pageable pageable);
    
    BlogPostResponse getBlogPostById(Long id);
    
    List<BlogPostResponse> searchBlogPosts(String searchTerm);
    
    BlogPostResponse createBlogPost(Long userId, CreateBlogPostRequest request);
    
    List<BlogPostResponse> getBlogPostsByUser(Long userId);
}

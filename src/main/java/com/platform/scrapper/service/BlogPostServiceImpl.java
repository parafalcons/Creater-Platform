package com.platform.scrapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.scrapper.domain.BlogPost;
import com.platform.scrapper.repository.BlogPostRepository;
import com.platform.scrapper.request.CreateBlogPostRequest;
import com.platform.scrapper.response.BlogPostResponse;
import com.platform.user.domain.SystemUser;
import com.platform.user.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogPostServiceImpl implements BlogPostService {
    
    private final BlogPostRepository blogPostRepository;
    private final SystemUserRepository systemUserRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<BlogPostResponse> getAllBlogPosts() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToBlogPostResponse)
                .toList();
    }
    
    @Override
    public Page<BlogPostResponse> getBlogPostsPaginated(Pageable pageable) {
        return blogPostRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToBlogPostResponse);
    }
    
    @Override
    public BlogPostResponse getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found with id: " + id));
        return mapToBlogPostResponse(blogPost);
    }
    
    @Override
    public List<BlogPostResponse> searchBlogPosts(String searchTerm) {
        return blogPostRepository.searchBlogPosts(searchTerm)
                .stream()
                .map(this::mapToBlogPostResponse)
                .toList();
    }
    
    @Override
    @Transactional
    public BlogPostResponse createBlogPost(Long userId, CreateBlogPostRequest request) {
        // Convert attachments list to JSON string
        String attachmentsJson = null;
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            try {
                attachmentsJson = objectMapper.writeValueAsString(request.getAttachments());
            } catch (JsonProcessingException e) {
                log.error("Error serializing attachments: {}", e.getMessage());
                throw new RuntimeException("Error processing attachments");
            }
        }
        
        BlogPost blogPost = BlogPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .attachments(attachmentsJson)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        BlogPost savedBlogPost = blogPostRepository.save(blogPost);
        return mapToBlogPostResponse(savedBlogPost);
    }
    
    @Override
    public List<BlogPostResponse> getBlogPostsByUser(Long userId) {
        return blogPostRepository.findByCreatedByOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToBlogPostResponse)
                .toList();
    }
    
    private BlogPostResponse mapToBlogPostResponse(BlogPost blogPost) {
        // Get username for createdBy
        String createdByUsername = getUsernameById(blogPost.getCreatedBy());
        
        // Parse attachments JSON to List<String>
        List<String> attachments = new ArrayList<>();
        if (blogPost.getAttachments() != null && !blogPost.getAttachments().isEmpty()) {
            try {
                attachments = objectMapper.readValue(blogPost.getAttachments(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                log.error("Error parsing attachments JSON: {}", e.getMessage());
            }
        }
        
        return BlogPostResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .description(blogPost.getDescription())
                .attachments(attachments)
                .createdBy(blogPost.getCreatedBy())
                .createdByUsername(createdByUsername)
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .build();
    }
    
    private String getUsernameById(Long userId) {
        // Check if it's a system user (website ID)
        if (userId == 1L) {
            return "System";
        }
        
        // For now, return a generic name. In a real implementation, you'd fetch from UserRepository
        return "User-" + userId;
    }
}

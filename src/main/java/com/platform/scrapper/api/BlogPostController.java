package com.platform.scrapper.api;

import com.platform.scrapper.request.CreateBlogPostRequest;
import com.platform.scrapper.response.BlogPostResponse;
import com.platform.scrapper.service.BlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/blog-posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BlogPostController {
    
    private final BlogPostService blogPostService;
    
    @GetMapping
    public ResponseEntity<List<BlogPostResponse>> getAllBlogPosts() {
        List<BlogPostResponse> blogPosts = blogPostService.getAllBlogPosts();
        return ResponseEntity.ok(blogPosts);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<BlogPostResponse>> getBlogPostsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPostResponse> blogPosts = blogPostService.getBlogPostsPaginated(pageable);
        return ResponseEntity.ok(blogPosts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostResponse> getBlogPostById(@PathVariable Long id) {
        BlogPostResponse blogPost = blogPostService.getBlogPostById(id);
        return ResponseEntity.ok(blogPost);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BlogPostResponse>> searchBlogPosts(@RequestParam String q) {
        List<BlogPostResponse> blogPosts = blogPostService.searchBlogPosts(q);
        return ResponseEntity.ok(blogPosts);
    }
    
    @PostMapping
    public ResponseEntity<BlogPostResponse> createBlogPost(
            Authentication authentication,
            @Valid @RequestBody CreateBlogPostRequest request) {
        
        Long userId = Long.parseLong(authentication.getName());
        BlogPostResponse blogPost = blogPostService.createBlogPost(userId, request);
        return ResponseEntity.ok(blogPost);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BlogPostResponse>> getBlogPostsByUser(@PathVariable Long userId) {
        List<BlogPostResponse> blogPosts = blogPostService.getBlogPostsByUser(userId);
        return ResponseEntity.ok(blogPosts);
    }
}

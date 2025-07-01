package com.platform.post.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.post.request.CreatePostRequest;
import com.platform.post.response.PostResponse;
import com.platform.post.service.PostService;
import com.platform.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request, 
                                                   HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        PostResponse response = postService.createPost(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId, 
                                               HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        PostResponse response = postService.getPostById(postId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long userId, 
                                                          HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long currentUserId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        List<PostResponse> posts = postService.getUserPosts(userId, currentUserId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        List<PostResponse> posts = postService.getFeedPosts(userId, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String q, 
                                                         HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        List<PostResponse> posts = postService.searchPosts(q, userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> likePost(@PathVariable Long postId, 
                                           HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        boolean result = postService.likePost(postId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<Boolean> unlikePost(@PathVariable Long postId, 
                                             HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        boolean result = postService.unlikePost(postId, userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long postId, 
                                             HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        boolean result = postService.deletePost(postId, userId);
        return ResponseEntity.ok(result);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Invalid token");
    }
} 
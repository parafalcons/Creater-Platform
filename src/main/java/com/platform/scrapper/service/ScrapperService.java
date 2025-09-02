package com.platform.scrapper.service;

import com.platform.scrapper.request.AddWebsiteRequest;
import com.platform.scrapper.request.CreateBlogPostRequest;
import com.platform.scrapper.request.UpdateBlogPostRequest;
import com.platform.scrapper.response.BlogPostResponse;
import com.platform.scrapper.response.WebsiteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ScrapperService {
    
    // Website management
    WebsiteResponse addWebsite(AddWebsiteRequest request);
    List<WebsiteResponse> getAllWebsites();
    WebsiteResponse getWebsiteById(Long id);
    void deleteWebsite(Long id);
    void toggleWebsiteStatus(Long id);
    
    // Blog post management
    List<BlogPostResponse> getAllBlogPosts();
    Page<BlogPostResponse> getBlogPostsPaginated(Pageable pageable);
    List<BlogPostResponse> getBlogPostsByWebsite(Long websiteId);
    List<BlogPostResponse> getBlogPostsByUser(Long userId);
    List<BlogPostResponse> searchBlogPosts(String searchTerm);
    BlogPostResponse getBlogPostById(Long id);
    BlogPostResponse createBlogPost(Long userId, CreateBlogPostRequest request, MultipartFile image);
    BlogPostResponse updateBlogPost(Long userId, Long blogPostId, UpdateBlogPostRequest request);
    void deleteBlogPost(Long userId, Long blogPostId);
    
    // Scraping operations
    void scrapeAllWebsites();
    void scrapeWebsite(Long websiteId);
    void scrapeWebsiteByUrl(String url);
    
    // Statistics
    long getTotalBlogPosts();
    long getTotalWebsites();
    long getActiveWebsites();
    long getTotalUsers();
}

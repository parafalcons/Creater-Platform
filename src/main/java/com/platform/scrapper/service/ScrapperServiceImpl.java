package com.platform.scrapper.service;

import com.platform.scrapper.domain.Website;
import com.platform.scrapper.repository.WebsiteRepository;
import com.platform.scrapper.request.AddWebsiteRequest;
import com.platform.scrapper.request.CreateBlogPostRequest;
import com.platform.scrapper.request.UpdateBlogPostRequest;
import com.platform.scrapper.response.BlogPostResponse;
import com.platform.scrapper.response.WebsiteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapperServiceImpl implements ScrapperService {
    
    private final WebsiteRepository websiteRepository;
    
    @Override
    public List<WebsiteResponse> getAllWebsites() {
        return websiteRepository.findAll().stream()
                .map(this::mapToWebsiteResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public WebsiteResponse getWebsiteById(Long id) {
        Website website = websiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Website not found with id: " + id));
        return mapToWebsiteResponse(website);
    }
    
    @Override
    @Transactional
    public WebsiteResponse addWebsite(AddWebsiteRequest request) {
        // Check if website already exists
        if (websiteRepository.existsByUrl(request.getUrl())) {
            throw new RuntimeException("Website with URL already exists: " + request.getUrl());
        }
        
        Website website = Website.builder()
                .name(request.getName())
                .url(request.getUrl())
                .isActive(true)
                .build();
        
        Website savedWebsite = websiteRepository.save(website);
        log.info("Website added successfully: {}", savedWebsite.getUrl());
        return mapToWebsiteResponse(savedWebsite);
    }
    
    @Override
    @Transactional
    public void deleteWebsite(Long id) {
        Website website = websiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Website not found with id: " + id));
        
        websiteRepository.delete(website);
        log.info("Website deleted successfully: {}", website.getUrl());
    }
    
    @Override
    @Transactional
    public void toggleWebsiteStatus(Long id) {
        Website website = websiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Website not found with id: " + id));
        
        // website.setIsActive(!website.getIsActive());
        website.setUpdatedAt(LocalDateTime.now());
        websiteRepository.save(website);
        
        // log.info("Website status toggled: {} - Active: {}", website.getUrl(), website.getIsActive());
    }
    
    private WebsiteResponse mapToWebsiteResponse(Website website) {
        return WebsiteResponse.builder()
                .id(website.getId())
                .name(website.getName())
                .url(website.getUrl())
                // .isActive(website.getIsActive())
                .createdAt(website.getCreatedAt())
                // .updatedAt(website.getUpdatedAt())
                .lastScrapedAt(website.getLastScrapedAt())
                .build();
    }

    @Override
    public List<BlogPostResponse> getAllBlogPosts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllBlogPosts'");
    }

    @Override
    public Page<BlogPostResponse> getBlogPostsPaginated(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlogPostsPaginated'");
    }

    @Override
    public List<BlogPostResponse> getBlogPostsByWebsite(Long websiteId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlogPostsByWebsite'");
    }

    @Override
    public List<BlogPostResponse> getBlogPostsByUser(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlogPostsByUser'");
    }

    @Override
    public List<BlogPostResponse> searchBlogPosts(String searchTerm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchBlogPosts'");
    }

    @Override
    public BlogPostResponse getBlogPostById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlogPostById'");
    }

    @Override
    public BlogPostResponse createBlogPost(Long userId, CreateBlogPostRequest request, MultipartFile image) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBlogPost'");
    }

    @Override
    public BlogPostResponse updateBlogPost(Long userId, Long blogPostId, UpdateBlogPostRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBlogPost'");
    }

    @Override
    public void deleteBlogPost(Long userId, Long blogPostId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBlogPost'");
    }

    @Override
    public void scrapeAllWebsites() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scrapeAllWebsites'");
    }

    @Override
    public void scrapeWebsite(Long websiteId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scrapeWebsite'");
    }

    @Override
    public void scrapeWebsiteByUrl(String url) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scrapeWebsiteByUrl'");
    }

    @Override
    public long getTotalBlogPosts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalBlogPosts'");
    }

    @Override
    public long getTotalWebsites() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalWebsites'");
    }

    @Override
    public long getActiveWebsites() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getActiveWebsites'");
    }

    @Override
    public long getTotalUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalUsers'");
    }
}

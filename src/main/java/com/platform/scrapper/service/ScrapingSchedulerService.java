package com.platform.scrapper.service;

import com.platform.scrapper.BlogScraper;
import com.platform.scrapper.domain.BlogPost;
import com.platform.scrapper.domain.Website;
import com.platform.scrapper.repository.BlogPostRepository;
import com.platform.scrapper.repository.WebsiteRepository;
import com.platform.user.domain.SystemUser;
import com.platform.user.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingSchedulerService {
    
    private final WebsiteRepository websiteRepository;
    private final BlogPostRepository blogPostRepository;
    private final SystemUserRepository systemUserRepository;
    
    private static final Long SYSTEM_USER_ID = 1L; // Default system user ID
    
    /**
     * Scheduled task that runs every minute to scrape all active websites
     * Cron expression: "0 * * * * *" = every minute
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void scrapeAllWebsitesScheduled() {
        log.info("Starting scheduled scraping of all active websites...");
        
        try {
            // Get all active websites
            List<Website> activeWebsites = websiteRepository.findByIsActiveTrue();
            
            if (activeWebsites.isEmpty()) {
                log.info("No active websites found for scraping");
                return;
            }
            
            log.info("Found {} active websites to scrape", activeWebsites.size());
            
            // Scrape each website
            for (Website website : activeWebsites) {
                try {
                    scrapeWebsiteWithDuplicatePrevention(website);
                } catch (Exception e) {
                    log.error("Error scraping website {}: {}", website.getUrl(), e.getMessage());
                    // Continue with next website even if one fails
                }
            }
            
            log.info("Scheduled scraping completed successfully");
            
        } catch (Exception e) {
            log.error("Error in scheduled scraping: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Scrape a website and save new blog posts, avoiding duplicates
     */
    private void scrapeWebsiteWithDuplicatePrevention(Website website) {
        log.info("Scraping website: {}", website.getUrl());
        
        try {
            // Use the existing BlogScraper to get blog posts
            List<BlogScraper.BlogPost> scrapedPosts = BlogScraper.scrapeBlogsFromWebsite(website.getUrl());
            
            if (scrapedPosts.isEmpty()) {
                log.info("No blog posts found for website: {}", website.getUrl());
                return;
            }
            
            log.info("Found {} blog posts from website: {}", scrapedPosts.size(), website.getUrl());
            
            int newPostsCount = 0;
            int duplicatePostsCount = 0;
            
            for (BlogScraper.BlogPost scrapedPost : scrapedPosts) {
                // Check if blog post already exists by title and website (createdBy)
                if (!blogPostRepository.existsByTitleAndCreatedBy(scrapedPost.getBlogTitle(), website.getId())) {
                    
                    // Create new blog post
                    BlogPost blogPost = BlogPost.builder()
                            .title(scrapedPost.getBlogTitle())
                            .description(scrapedPost.getExcerpt())
                            .createdBy(website.getId()) // Use website ID as createdBy for system scraped posts
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    
                    blogPostRepository.save(blogPost);
                    newPostsCount++;
                    
                    log.debug("Saved new blog post: {}", scrapedPost.getBlogTitle());
                } else {
                    duplicatePostsCount++;
                    log.debug("Skipped duplicate blog post: {}", scrapedPost.getBlogTitle());
                }
            }
            
            // Update website last scraped timestamp
            website.setLastScrapedAt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            websiteRepository.save(website);
            
            log.info("Website {} scraping completed - New posts: {}, Duplicates skipped: {}", 
                    website.getUrl(), newPostsCount, duplicatePostsCount);
            
        } catch (Exception e) {
            log.error("Error scraping website {}: {}", website.getUrl(), e.getMessage());
            throw new RuntimeException("Failed to scrape website: " + website.getUrl(), e);
        }
    }
    
    /**
     * Manual trigger for scraping (can be called via API if needed)
     */
    @Transactional
    public void triggerManualScraping() {
        log.info("Manual scraping triggered");
        scrapeAllWebsitesScheduled();
    }
    
    /**
     * Get scraping statistics
     */
    public ScrapingStatistics getScrapingStatistics() {
        List<Website> activeWebsites = websiteRepository.findByIsActiveTrue();
        long totalBlogPosts = blogPostRepository.count();
        
        return ScrapingStatistics.builder()
                .activeWebsites(activeWebsites.size())
                .totalBlogPosts(totalBlogPosts)
                .lastScrapingRun(LocalDateTime.now())
                .build();
    }
    
    /**
     * Statistics class for scraping information
     */
    @lombok.Data
    @lombok.Builder
    public static class ScrapingStatistics {
        private int activeWebsites;
        private long totalBlogPosts;
        private LocalDateTime lastScrapingRun;
    }
}

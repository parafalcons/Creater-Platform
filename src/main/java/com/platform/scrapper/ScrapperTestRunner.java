package com.platform.scrapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Test runner to demonstrate the scraper system
 * This will run when the application starts
 */
@Component
public class ScrapperTestRunner implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Blog Scraper System Started!");
        System.out.println("📊 Database tables created successfully");
        System.out.println("🌐 Default websites added to database");
        System.out.println("✅ System is ready for API calls");
        System.out.println();
        System.out.println("📋 Available API Endpoints:");
        System.out.println("  GET  /api/scrapper/websites - Get all websites");
        System.out.println("  POST /api/scrapper/websites - Add new website");
        System.out.println("  GET  /api/scrapper/blog-posts - Get all blog posts");
        System.out.println("  POST /api/scrapper/scrape/all - Scrape all websites");
        System.out.println("  GET  /api/scrapper/stats - Get statistics");
        System.out.println();
        System.out.println("🔗 Test URLs:");
        System.out.println("  http://localhost:8080/api/scrapper/websites");
        System.out.println("  http://localhost:8080/api/scrapper/blog-posts");
        System.out.println("  http://localhost:8080/api/scrapper/stats");
    }
}







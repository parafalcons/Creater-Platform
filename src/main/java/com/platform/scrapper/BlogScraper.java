package com.platform.scrapper;

import lombok.Builder;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

@Component
public class BlogScraper {

    @Builder
    @Data
    public static class BlogPost {
        public String blogTitle;
        public String originalLink;
        public String author;
        public String publishDate;
        public String readTime;
        public String excerpt;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("=== Blog Scraper Initialized ===");
    }

    /**
     * Removes duplicate blog posts based on URL
     * @param blogPosts List of blog posts that may contain duplicates
     * @return List of unique blog posts
     */
    public static List<BlogPost> removeDuplicateBlogPosts(List<BlogPost> blogPosts) {
        if (blogPosts == null || blogPosts.isEmpty()) {
            return blogPosts;
        }
        
        // Use LinkedHashMap to maintain order and remove duplicates
        java.util.LinkedHashMap<String, BlogPost> uniquePosts = new java.util.LinkedHashMap<>();
        
        for (BlogPost post : blogPosts) {
            if (post != null && post.getOriginalLink() != null && !post.getOriginalLink().isEmpty()) {
                // Use the original link as the key to identify duplicates
                uniquePosts.putIfAbsent(post.getOriginalLink(), post);
            }
        }
        
        System.out.println("Removed " + (blogPosts.size() - uniquePosts.size()) + " duplicate blog posts");
        return new ArrayList<>(uniquePosts.values());
    }

    /**
     * Scrapes blog posts from a list of websites
     * @param websiteList List of website URLs to scrape
     * @return List of BlogPost objects containing blog_title and original_link
     */
    public static List<BlogPost> scrapeBlogsFromWebsites(List<String> websiteList) {
        List<BlogPost> allBlogPosts = new ArrayList<>();
        
        for (String websiteUrl : websiteList) {
            System.out.println("Scraping: " + websiteUrl);
            try {
                List<BlogPost> posts = scrapeBlogsFromWebsite(websiteUrl);
                allBlogPosts.addAll(posts);
                System.out.println("Found " + posts.size() + " blog posts from " + websiteUrl);
            } catch (Exception e) {
                System.err.println("Error scraping " + websiteUrl + ": " + e.getMessage());
            }
        }
        
        return allBlogPosts;
    }

    /**
     * Scrapes blog posts from a single website
     * @param websiteUrl The URL of the website to scrape
     * @return List of BlogPost objects
     */
    public static List<BlogPost> scrapeBlogsFromWebsite(String websiteUrl) {
        List<BlogPost> blogPosts = new ArrayList<>();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115 Safari/537.36");
        
        WebDriver driver = new ChromeDriver(options);
        
        try {
            System.out.println("Loading page: " + websiteUrl);
            driver.get(websiteUrl);
            
            // Wait for page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.tagName("body")));
            } catch (Exception ignored) {}
            
            // Scroll to load more content
            for (int i = 0; i < 3; i++) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }
            
            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource, websiteUrl);
            
            // Extract blog posts based on the website
            if (websiteUrl.contains("medium.com/blog")) {
                blogPosts = extractMediumBlogPosts(doc, websiteUrl);
            } else {
                blogPosts = extractGenericBlogPosts(doc, websiteUrl);
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping website: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { driver.quit(); } catch (Exception ignored) {}
        }
        
        return blogPosts;
    }

    /**
     * Extracts blog posts from Medium blog pages
     */
    private static List<BlogPost> extractMediumBlogPosts(Document doc, String websiteUrl) {
        List<BlogPost> posts = new ArrayList<>();
        
        try {
            // Medium blog post selectors
            String[] selectors = {
                "article", 
                "[data-testid='postPreview']",
                ".postArticle",
                ".post",
                "[class*='post']",
                "[class*='article']"
            };
            
            for (String selector : selectors) {
                List<Element> elements = doc.select(selector);
                if (!elements.isEmpty()) {
                    System.out.println("Found " + elements.size() + " posts with selector: " + selector);
                    
                    for (Element element : elements) {
                        BlogPost post = extractMediumBlogPost(element, websiteUrl);
                        if (post != null && !post.getBlogTitle().isEmpty()) {
                            posts.add(post);
                        }
                    }
                    break; // Use the first working selector
                }
            }
            
            // If no posts found with selectors, try link-based extraction
            if (posts.isEmpty()) {
                posts = extractMediumBlogPostsFromLinks(doc, websiteUrl);
            }
            
        } catch (Exception e) {
            System.err.println("Error extracting Medium blog posts: " + e.getMessage());
        }
        
        return posts;
    }

    /**
     * Extracts a single Medium blog post from an element
     */
    private static BlogPost extractMediumBlogPost(Element element, String websiteUrl) {
        try {
            // Extract title
            String title = "";
            Element titleElement = element.selectFirst("h1, h2, h3, h4, h5, h6, [data-testid='postPreviewTitle'], .title, [class*='title']");
            if (titleElement != null) {
                title = titleElement.text().trim();
            }
            
            // Extract link
            String link = "";
            Element linkElement = element.selectFirst("a[href]");
            if (linkElement != null) {
                String href = linkElement.attr("href");
                if (!href.isEmpty() && !href.equals("#")) {
                    // Clean up the href to remove duplicate usernames
                    href = cleanMediumUrl(href);
                    
                    if (href.startsWith("http")) {
                        link = href;
                    } else if (href.startsWith("/")) {
                        link = "https://medium.com" + href;
                    } else {
                        // For relative links, construct properly
                        link = "https://medium.com/" + href;
                    }
                }
            }
            
            // Extract author
            String author = "";
            Element authorElement = element.selectFirst("[data-testid='authorName'], .author, [class*='author']");
            if (authorElement != null) {
                author = authorElement.text().trim();
            }
            
            // Extract publish date
            String publishDate = "";
            Element dateElement = element.selectFirst("time, .date, [class*='date'], [datetime], [data-testid='publishDate'], [class*='publish']");
            if (dateElement != null) {
                publishDate = dateElement.attr("datetime");
                if (publishDate.isEmpty()) {
                    publishDate = dateElement.text().trim();
                }
            } else {
                // Try alternative selectors for date
                List<Element> dateElements = element.select("span, div, time");
                for (Element dateEl : dateElements) {
                    String text = dateEl.text().trim().toLowerCase();
                    // Look for date patterns like "Added 20h ago", "Jul 22", "2025", etc.
                    if (text.contains("ago") || text.matches(".*\\d{4}.*") || 
                        text.matches(".*(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*")) {
                        publishDate = dateEl.text().trim();
                        break;
                    }
                }
            }
            
            // Extract read time
            String readTime = "";
            Element readTimeElement = element.selectFirst("[data-testid='readTime'], .readTime, [class*='readTime'], [class*='time'], span:contains('min'), span:contains('read')");
            if (readTimeElement != null) {
                readTime = readTimeElement.text().trim();
            } else {
                // Try alternative selectors for read time
                List<Element> timeElements = element.select("span, div, time");
                for (Element timeEl : timeElements) {
                    String text = timeEl.text().trim().toLowerCase();
                    if (text.contains("min") && text.contains("read")) {
                        readTime = timeEl.text().trim();
                        break;
                    } else if (text.matches(".*\\d+\\s*min.*")) {
                        readTime = timeEl.text().trim();
                        break;
                    }
                }
            }
            
            // Extract excerpt
            String excerpt = "";
            Element excerptElement = element.selectFirst("[data-testid='postPreviewSnippet'], .excerpt, [class*='excerpt'], p");
            if (excerptElement != null) {
                excerpt = excerptElement.text().trim();
            }
            
            if (!title.isEmpty()) {
                BlogPost post = BlogPost.builder()
                    .blogTitle(title)
                    .originalLink(link.isEmpty() ? websiteUrl : link)
                    .author(author)
                    .publishDate(publishDate)
                    .readTime(readTime)
                    .excerpt(excerpt)
                    .build();
                
                // Debug: Print read time if found
                if (!readTime.isEmpty()) {
                    System.out.println("  Found read time: " + readTime + " for: " + title.substring(0, Math.min(50, title.length())));
                }
                
                // Debug: Print publish date if found
                if (!publishDate.isEmpty()) {
                    System.out.println("  Found publish date: " + publishDate + " for: " + title.substring(0, Math.min(50, title.length())));
                }
                
                return post;
            }
            
        } catch (Exception e) {
            System.err.println("Error extracting blog post: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Fallback method to extract Medium blog posts from links
     */
    private static List<BlogPost> extractMediumBlogPostsFromLinks(Document doc, String websiteUrl) {
        List<BlogPost> posts = new ArrayList<>();
        
        try {
            List<Element> links = doc.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                String title = link.text().trim();
                
                // Check if it's a valid blog post link
                if (!title.isEmpty() && title.length() > 10 && 
                    !href.isEmpty() && !href.equals("#") && 
                    (href.contains("/p/") || href.contains("/@") || href.startsWith("http"))) {
                    
                    // Clean up the href to remove duplicate usernames
                    href = cleanMediumUrl(href);
                    
                    String fullLink;
                    if (href.startsWith("http")) {
                        fullLink = href;
                    } else if (href.startsWith("/")) {
                        fullLink = "https://medium.com" + href;
                    } else {
                        fullLink = "https://medium.com/" + href;
                    }
                    
                    posts.add(BlogPost.builder()
                        .blogTitle(title)
                        .originalLink(fullLink)
                        .author("")
                        .publishDate("")
                        .readTime("")
                        .excerpt("")
                        .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting posts from links: " + e.getMessage());
        }
        
        return posts;
    }

    /**
     * Extracts blog posts from generic websites
     */
    private static List<BlogPost> extractGenericBlogPosts(Document doc, String websiteUrl) {
        List<BlogPost> posts = new ArrayList<>();
        
        try {
            // Generic blog post selectors
            String[] selectors = {
                "article", ".post", ".blog-post", ".entry", ".content-item",
                "[class*='post']", "[class*='article']", "[class*='blog']"
            };
            
            for (String selector : selectors) {
                List<Element> elements = doc.select(selector);
                if (!elements.isEmpty()) {
                    System.out.println("Found " + elements.size() + " posts with selector: " + selector);
                    
                    for (Element element : elements) {
                        BlogPost post = extractGenericBlogPost(element, websiteUrl);
                        if (post != null && !post.getBlogTitle().isEmpty()) {
                            posts.add(post);
                        }
                    }
                    break;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error extracting generic blog posts: " + e.getMessage());
        }
        
        return posts;
    }

    /**
     * Extracts a single generic blog post from an element
     */
    private static BlogPost extractGenericBlogPost(Element element, String websiteUrl) {
        try {
            // Extract title
            String title = "";
            Element titleElement = element.selectFirst("h1, h2, h3, h4, h5, h6, .title, [class*='title']");
            if (titleElement != null) {
                title = titleElement.text().trim();
            }
            
            // Extract link
            String link = "";
            Element linkElement = element.selectFirst("a[href]");
            if (linkElement != null) {
                String href = linkElement.attr("href");
                if (!href.isEmpty() && !href.equals("#")) {
                    link = href.startsWith("http") ? href : extractBaseUrl(websiteUrl) + href;
                }
            }
            
            if (!title.isEmpty()) {
                return BlogPost.builder()
                    .blogTitle(title)
                    .originalLink(link.isEmpty() ? websiteUrl : link)
                    .author("")
                    .publishDate("")
                    .readTime("")
                    .excerpt("")
                    .build();
            }
            
        } catch (Exception e) {
            System.err.println("Error extracting generic blog post: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Formats the publish date to be more readable
     */
    private static String formatPublishDate(String publishDate) {
        if (publishDate == null || publishDate.isEmpty()) {
            return "";
        }
        
        try {
            // Convert relative times to exact dates
            if (publishDate.contains("ago")) {
                return convertRelativeTimeToExactDate(publishDate);
            }
            
            // If it's already a nice format, return as is
            if (publishDate.matches(".*\\d{1,2}\\s*(min|hour|day|week|month|year).*")) {
                return convertRelativeTimeToExactDate(publishDate);
            }
            
            // Try to parse ISO date format
            if (publishDate.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                try {
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(publishDate.substring(0, 19));
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
                    return dateTime.format(formatter);
                } catch (Exception e) {
                    // If parsing fails, try just the date part
                    try {
                        java.time.LocalDate date = java.time.LocalDate.parse(publishDate.substring(0, 10));
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy");
                        return date.format(formatter);
                    } catch (Exception e2) {
                        // If all parsing fails, return original
                        return publishDate;
                    }
                }
            }
            
            // Try to parse other common formats
            if (publishDate.matches(".*\\d{4}.*")) {
                // Extract year and try to format
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\w{3})\\s*(\\d{1,2}),?\\s*(\\d{4})");
                java.util.regex.Matcher matcher = pattern.matcher(publishDate);
                if (matcher.find()) {
                    return matcher.group(1) + " " + matcher.group(2) + ", " + matcher.group(3);
                }
            }
            
        } catch (Exception e) {
            // If any formatting fails, return original
        }
        
        return publishDate;
    }
    
    /**
     * Converts relative time to exact date and time
     */
    private static String convertRelativeTimeToExactDate(String relativeTime) {
        try {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDateTime exactTime = now;
            
            // Parse relative time patterns
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*(min|hour|h|day|d|week|w|month|m|year|y)");
            java.util.regex.Matcher matcher = pattern.matcher(relativeTime.toLowerCase());
            
            if (matcher.find()) {
                int amount = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                
                switch (unit) {
                    case "min":
                        exactTime = now.minusMinutes(amount);
                        break;
                    case "hour":
                    case "h":
                        exactTime = now.minusHours(amount);
                        break;
                    case "day":
                    case "d":
                        exactTime = now.minusDays(amount);
                        break;
                    case "week":
                    case "w":
                        exactTime = now.minusWeeks(amount);
                        break;
                    case "month":
                    case "m":
                        exactTime = now.minusMonths(amount);
                        break;
                    case "year":
                    case "y":
                        exactTime = now.minusYears(amount);
                        break;
                }
                
                // Format the exact date and time
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
                return exactTime.format(formatter);
            }
            
        } catch (Exception e) {
            // If conversion fails, return original
        }
        
        return relativeTime;
    }

    /**
     * Extracts the base URL up to .com from a full URL
     */
    private static String extractBaseUrl(String websiteUrl) {
        if (websiteUrl == null || websiteUrl.isEmpty()) {
            return "";
        }
        
        try {
            // Find the position of .com
            int comIndex = websiteUrl.indexOf(".com");
            if (comIndex >= 0) {
                // Return the URL up to and including .com
                return websiteUrl.substring(0, comIndex + 4);
            }
            
            // If no .com found, try other common TLDs
            String[] tlds = {".org", ".net", ".edu", ".gov", ".io", ".co"};
            for (String tld : tlds) {
                int tldIndex = websiteUrl.indexOf(tld);
                if (tldIndex >= 0) {
                    return websiteUrl.substring(0, tldIndex + tld.length());
                }
            }
            
            // If no TLD found, return the original URL
            return websiteUrl;
        } catch (Exception e) {
            return websiteUrl;
        }
    }

    /**
     * Cleans Medium URLs to remove duplicate usernames
     */
    private static String cleanMediumUrl(String href) {
        if (href == null || href.isEmpty()) {
            return href;
        }
        
        // If the href contains duplicate @username patterns, clean it up
        if (href.contains("/@") && href.split("/@").length > 2) {
            // Find the first @username and keep only that part
            int firstAt = href.indexOf("/@");
            if (firstAt >= 0) {
                int secondAt = href.indexOf("/@", firstAt + 1);
                if (secondAt >= 0) {
                    // Remove the duplicate part
                    return href.substring(0, secondAt);
                }
            }
        }
        
        return href;
    }

    /**
     * Generates HTML file with the list of blog posts
     */
    public static void generateBlogListHTML(List<BlogPost> blogPosts, String outputFileName) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("    <title>Blog Posts List</title>\n")
                .append("    <style>\n")
                .append("        body { font-family: Arial, Helvetica, sans-serif; max-width: 1200px; margin: 0 auto; padding: 20px; background: #f7f7f7; }\n")
                .append("        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; text-align: center; }\n")
                .append("        .blog-post { background: white; border: 1px solid #e1e4e8; border-radius: 8px; padding: 20px; margin: 15px 0; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n")
                .append("        .blog-title { font-size: 18px; font-weight: bold; color: #2c3e50; margin-bottom: 10px; }\n")
                .append("        .blog-title a { color: inherit; text-decoration: none; }\n")
                .append("        .blog-title a:hover { color: #3498db; text-decoration: underline; }\n")
                .append("        .blog-meta { color: #7f8c8d; font-size: 14px; margin-bottom: 10px; }\n")
                .append("        .blog-excerpt { color: #2c3e50; line-height: 1.6; }\n")
                .append("        .meta { color: #7f8c8d; font-size: 13px; margin-bottom: 20px; text-align: center; background: #ecf0f1; padding: 15px; border-radius: 8px; }\n")
                .append("        .post-count { color: #27ae60; font-weight: bold; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h1>📝 Blog Posts List</h1>\n")
                .append("    <div class=\"meta\">\n")
                .append("        <p><strong>Total Blog Posts:</strong> <span class=\"post-count\">").append(blogPosts.size()).append("</span></p>\n")
                .append("        <p><strong>Generated on:</strong> ").append(java.time.LocalDateTime.now().toString()).append("</p>\n")
                .append("    </div>\n");

            if (blogPosts.isEmpty()) {
                html.append("    <div class=\"blog-post\">\n")
                    .append("        <p>No blog posts found. Please check the website URLs and try again.</p>\n")
                    .append("    </div>\n");
            } else {
                for (BlogPost post : blogPosts) {
                    html.append("    <div class=\"blog-post\">\n")
                        .append("        <div class=\"blog-title\">\n")
                        .append("            <a href=\"").append(escapeHtml(post.getOriginalLink())).append("\" target=\"_blank\">\n")
                        .append("                ").append(escapeHtml(post.getBlogTitle())).append("\n")
                        .append("            </a>\n")
                        .append("        </div>\n");
                    
                    // Add meta information (author and date only)
                    if (!post.getAuthor().isEmpty() || !post.getPublishDate().isEmpty()) {
                        html.append("        <div class=\"blog-meta\">\n");
                        if (!post.getAuthor().isEmpty()) {
                            html.append("            <span style=\"background: #3498db; color: white; padding: 4px 8px; border-radius: 4px; margin-right: 8px; font-size: 12px;\">👤 ").append(escapeHtml(post.getAuthor())).append("</span>\n");
                        }
                        if (!post.getPublishDate().isEmpty()) {
                            html.append("            <span style=\"background: #27ae60; color: white; padding: 4px 8px; border-radius: 4px; margin-right: 8px; font-size: 12px;\">📅 ").append(escapeHtml(formatPublishDate(post.getPublishDate()))).append("</span>\n");
                        }
                        html.append("        </div>\n");
                    }
                    
                    html.append("    </div>\n");
                }
            }
            
            html.append("</body>\n")
                .append("</html>");

            // Save to file
            File file = new File(outputFileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(html.toString());
            }
            
            System.out.println("✅ HTML file generated successfully!");
            System.out.println("📁 File saved as: " + file.getAbsolutePath());
            System.out.println("🌐 Opening file in browser...");
            
            // Open in browser
            try {
                java.awt.Desktop.getDesktop().browse(file.toURI());
            } catch (IOException e) {
                System.out.println("Could not open browser automatically. File saved as: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            System.err.println("Error generating HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility method to escape HTML characters
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Main method to run the blog scraper
     */
    public static void main(String[] args) {
        System.out.println("=== Blog Scraper ===");
        
        // List of websites to scrape
        List<String> websites = new ArrayList<>();
        websites.add("https://medium.com/blog/all");
        websites.add("https://medium.com/@shivambhadani_");
        // websites.add("https://aditi22aggarwal-23582.medium.com/");
        // websites.add("https://bytebytego.com/courses/system-design-interview/scale-from-zero-to-millions-of-users");




        // Add more websites as needed
        // websites.add("https://example.com/blog");
        
        try {
            // Scrape blogs from all websites
            System.out.println("Scraping blogs from " + websites.size() + " website(s)...");
            List<BlogPost> blogPosts = scrapeBlogsFromWebsites(websites);
            
            // Remove duplicates
            blogPosts = removeDuplicateBlogPosts(blogPosts);
            
            System.out.println("Total blog posts found: " + blogPosts.size());
            
            // Generate HTML file
            String outputFileName = "blog_posts_" + System.currentTimeMillis() + ".html";
            generateBlogListHTML(blogPosts, outputFileName);
            
        } catch (Exception e) {
            System.err.println("Error running blog scraper: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

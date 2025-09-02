package com.platform.scrapper.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteResponse {
    
    private Long id;
    private String url;
    private String name;
    private String description;
    private boolean isActive;
    private boolean isScraped;
    private String lastScrapedAt;
    private Integer totalPostsScraped;
    private LocalDateTime createdAt;
}


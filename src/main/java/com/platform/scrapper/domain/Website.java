package com.platform.scrapper.domain;

import com.platform.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "website")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Website extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String url;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    @Column(nullable = false)
    private boolean isScraped = false;
    
    @Column
    private String lastScrapedAt;
    
    @Column
    private Integer totalPostsScraped = 0;
}


package com.platform.scrapper.repository;

import com.platform.scrapper.domain.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, Long> {
    
    List<Website> findByIsActiveTrue();
    
    List<Website> findByIsActiveTrueAndIsScrapedFalse();
    
    boolean existsByUrl(String url);
    
    Website findByUrl(String url);
}


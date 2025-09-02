package com.platform.scrapper.repository;

import com.platform.scrapper.domain.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    List<BlogPost> findAllByOrderByCreatedAtDesc();
    
    Page<BlogPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<BlogPost> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    @Query("SELECT bp FROM BlogPost bp WHERE bp.title LIKE %:searchTerm% OR bp.description LIKE %:searchTerm% ORDER BY bp.createdAt DESC")
    List<BlogPost> searchBlogPosts(@Param("searchTerm") String searchTerm);
    
    // Check if blog post exists by title and creator (for duplicate prevention)
    boolean existsByTitleAndCreatedBy(String title, Long createdBy);
    
    // Find by title and creator
    Optional<BlogPost> findByTitleAndCreatedBy(String title, Long createdBy);
}


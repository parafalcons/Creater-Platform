package com.platform.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.post.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT p FROM Post p WHERE p.userId IN :userIds ORDER BY p.createdAt DESC")
    List<Post> findByUserIdsOrderByCreatedAtDesc(@Param("userIds") List<Long> userIds);
    
    @Query("SELECT p FROM Post p WHERE p.caption LIKE %:searchTerm% OR p.userName LIKE %:searchTerm% ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
} 
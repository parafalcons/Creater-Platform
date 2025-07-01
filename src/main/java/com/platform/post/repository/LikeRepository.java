package com.platform.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.post.domain.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
    
    boolean existsByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
    
    List<Like> findByTargetIdAndTargetType(Long targetId, String targetType);
    
    Long countByTargetIdAndTargetType(Long targetId, String targetType);
    
    void deleteByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
    
    @Modifying
    @Query("DELETE FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType")
    void deleteByTargetIdAndTargetType(@Param("targetId") Long targetId, @Param("targetType") String targetType);
} 
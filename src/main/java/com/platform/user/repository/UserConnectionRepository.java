package com.platform.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.user.domain.UserConnection;

public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {

    List<UserConnection> findByUserId(Long userId);

    List<UserConnection> findByFollowerId(Long userId);

 
}

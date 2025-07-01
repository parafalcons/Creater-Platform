package com.platform.user.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.user.domain.User;
import com.platform.user.domain.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    UserProfile findByUserId(Long userId);

}

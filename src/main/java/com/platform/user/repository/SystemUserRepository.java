package com.platform.user.repository;

import com.platform.user.domain.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    
    Optional<SystemUser> findByUsername(String username);
    
    Optional<SystemUser> findByEmail(String email);
    
    Optional<SystemUser> findByIsSystemUserTrue();
}

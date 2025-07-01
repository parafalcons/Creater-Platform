package com.platform.user.domain;

import com.platform.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_connection")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConnection extends BaseEntity {
    
    private Long userId;
    private String photo;
    private String fullName;
    private String userName;
    private Long followerId;
    private String followerPhoto;
    private String followerFullName;
    private String followerUserName;
    private boolean isFollowing;

     
}

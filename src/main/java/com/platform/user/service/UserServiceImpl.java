package com.platform.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.user.domain.User;
import com.platform.user.domain.UserConnection;
import com.platform.user.domain.UserProfile;
import com.platform.user.repository.UserConnectionRepository;
import com.platform.user.repository.UserProfileRepository;
import com.platform.user.repository.UserRepository;
import com.platform.user.request.CreateUserRequest;
import com.platform.user.request.UpdateProfileRequest;
import com.platform.user.response.UserConnectionResponse;
import com.platform.user.response.UserProfileResponse;
import com.platform.user.response.UserResponse;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserConnectionRepository userConnectionRepository;


    

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Transactional
    @Override
    public boolean createUser(CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            return false; // Username already exists
        }

        User user = User.builder()
                .fullName(request.getName())
                .userName(request.getUserName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword())
                .build();

        user = userRepository.save(user);
        UserProfile userProfile = UserProfile.builder().userId(user.getId()).followers(0l).followings(0l).build();
        userProfileRepository.save(userProfile);

        return true;
    }

    @Override
    public User getUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }

    @Override
    public User getUserByUserName(String username) {
        User user = userRepository.findByUserName(username);
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public UserProfileResponse getUserProfileDetails(Long userId) {
        UserProfile user = userProfileRepository.findByUserId(userId);
        UserProfileResponse response=UserProfileResponse.builder().build();
        BeanUtils.copyProperties(user, response);
        return response;

    }

    @Override
public List<UserConnectionResponse> getUserConnections(Long userId, boolean followers) {
    List<UserConnection> connections;
    List<UserConnectionResponse> response = new ArrayList<>();

    if (followers) {
        // People who follow the user
        connections = userConnectionRepository.findByUserId(userId);
        response = connections.stream()
            .map(conn -> UserConnectionResponse.builder()
                .photo(conn.getFollowerPhoto())
                .username(conn.getFollowerUserName())
                .name(conn.getFollowerFullName())
                .connectionId(conn.getFollowerId())
                .isFollowing(conn.isFollowing())
                .build())
            .collect(Collectors.toList());
    } else {
        // People the user is following
        connections = userConnectionRepository.findByFollowerId(userId);
        response = connections.stream()
            .map(conn -> UserConnectionResponse.builder()
                .photo(conn.getPhoto())
                .username(conn.getUserName())
                .name(conn.getFullName())
                .connectionId(conn.getUserId())
                .build())
            .collect(Collectors.toList());
    }

    return response;
}

    @Override
    public Boolean follow(Long userId, Long followingId) {
        User user=userRepository.findById(userId).get();
        User following=userRepository.findById(followingId).get();

        UserConnection  userConnection = UserConnection.builder()
                                         .userId(userId)
                                         .userName(user.getUserName())
                                         .fullName(user.getFullName())
                                         .photo(null)
                                         .followerId(followingId)
                                         .followerFullName(following.getFullName())
                                         .followerUserName(following.getUserName())
                                         .followerPhoto(null)
                                         .isFollowing(true)
                                       .build();
        userConnectionRepository.save(userConnection);

        return true;

    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update user fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        userRepository.save(user);
        
        // Update user profile
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        if (userProfile == null) {
            userProfile = UserProfile.builder()
                    .userId(userId)
                    .followers(0L)
                    .followings(0L)
                    .build();
        }
        
        if (request.getBio() != null) {
            userProfile.setBio(request.getBio());
        }
        if (request.getProfilePhotoUrl() != null) {
            // TODO: Add profile photo field to UserProfile entity
            // userProfile.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }
        
        userProfileRepository.save(userProfile);
        
        // Return updated profile
        UserProfileResponse response = new UserProfileResponse();
        BeanUtils.copyProperties(userProfile, response);
        return response;
    }

}
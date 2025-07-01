package com.platform.user.service;

import java.util.List;

import com.platform.user.domain.User;
import com.platform.user.request.CreateUserRequest;
import com.platform.user.request.UpdateProfileRequest;
import com.platform.user.response.UserConnectionResponse;
import com.platform.user.response.UserProfileResponse;
import com.platform.user.response.UserResponse;

public interface UserService {


    boolean existsByUserName(String userName);

    boolean createUser(CreateUserRequest request);

    User getUserByUserName(String userName);

    User getUserByUserId(Long userId);

    User getUserByEmail(String email);

    User updateUser(User user);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    UserProfileResponse getUserProfileDetails(Long userId);

    List<UserConnectionResponse> getUserConnections(Long userId ,boolean followers);

    Boolean follow(Long userId, Long followingId);
    
}

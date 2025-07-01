package com.platform.user.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.user.domain.User;
import com.platform.user.request.CreateUserRequest;
import com.platform.user.request.UpdateProfileRequest;
import com.platform.user.response.UserConnectionResponse;
import com.platform.user.response.UserProfileResponse;
import com.platform.user.response.UserResponse;
import com.platform.user.service.UserService;
import com.platform.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApiResource {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String userName) {
        boolean exists = userService.existsByUserName(userName);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<Boolean> createUser(@RequestBody CreateUserRequest request) {
        boolean created = userService.createUser(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping()
    public ResponseEntity<User> getUserByUserName(@RequestParam Long userId) {
        User user = userService.getUserByUserId(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestParam Long userId) {
        UserProfileResponse userProfileResponse = userService.getUserProfileDetails(userId);
        return ResponseEntity.ok(userProfileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request,
                                                           HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{userId}/connections")
    public ResponseEntity<List<UserConnectionResponse>> getUserConnections(@PathVariable("userId") Long userId,@RequestParam boolean followers) {
        List<UserConnectionResponse> userConnectionResponse = userService.getUserConnections(userId,followers);
        return ResponseEntity.ok(userConnectionResponse);
    }

    @GetMapping("{userId}/follow/{followingId}")
    public ResponseEntity<Boolean> follow(@PathVariable("userId") Long userId,@PathVariable("followingId") Long followingId) {
        return ResponseEntity.ok(userService.follow(userId,followingId));
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Invalid token");
    }
}

package com.platform.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.auth.request.LoginRequest;
import com.platform.auth.request.SignupRequest;
import com.platform.security.JwtTokenProvider;
import com.platform.user.domain.PasswordResetToken;
import com.platform.user.domain.User;
import com.platform.user.repository.PasswordResetTokenRepository;
import com.platform.user.request.CreateUserRequest;
import com.platform.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public boolean registerUser(SignupRequest request) {
        // Check if user exists in User Service
        boolean exists = userService.existsByUserName(request.getUserName());
        if (exists)
            return false;

        // Hash the password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Save the user in User Service
        CreateUserRequest userRequest= CreateUserRequest.builder()
                                        .name(request.getFullName())
                                        .email(request.getEmail())
                                        .phoneNumber(request.getPhoneNumber())
                                        .password(hashedPassword)
                                        .userName(request.getUserName())
                                       .build();
        userService.createUser(userRequest);
        return true;
    }

    public String authenticateUser(LoginRequest request) {
        // Fetch user from User Service
        User user = userService.getUserByUserName(request.getUserName());

        // Validate password
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        return jwtTokenProvider.createToken(user.getUserName());
    }

    public boolean checkUserExists(String userName) {
        return userService.existsByUserName(userName);
    }

    public void logout(HttpServletRequest request) {
        // TODO: Implement token blacklisting if needed
        // For now, just return success
    }

    public boolean requestPasswordReset(String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return false;
        }

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Create new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(24)) // 24 hours expiry
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // TODO: Send email with reset link
        // For now, just print to console
        System.out.println("Password reset link: http://localhost:8080/reset-password?token=" + token);

        return true;
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElse(null);

        if (resetToken == null || resetToken.isUsed() || 
            resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Update user password
        User user = userService.getUserByUserId(resetToken.getUserId());
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        return true;
    }
}

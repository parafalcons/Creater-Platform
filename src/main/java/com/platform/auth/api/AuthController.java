package com.platform.auth.api;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.auth.request.LoginRequest;
import com.platform.auth.request.SignupRequest;
import com.platform.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        log.info("=== TEST ENDPOINT ACCESSED SUCCESSFULLY ===");
        return ResponseEntity.ok("Auth controller is working! Current time: " + System.currentTimeMillis());
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String userName) {
        log.info("Checking if user exists: {}", userName);
        boolean exists = authService.checkUserExists(userName);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequest request) {
        log.info("=== SIGNUP REQUEST RECEIVED ===");
        log.info("Signup request received for user: {}", request.getUsername());
        try {
            boolean isRegistered = authService.registerUser(request);
            if (isRegistered) {
                log.info("User registered successfully: {}", request.getUsername());
                return ResponseEntity.ok("User registered successfully");
            } else {
                log.warn("User registration failed - user already exists: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        log.info("=== LOGIN REQUEST RECEIVED ===");
        log.info("Login request received for user: {}", request.getUsernameOrEmail());
        try {
            String token = authService.authenticateUser(request);
            if (token != null) {
                log.info("User logged in successfully: {}", request.getUsernameOrEmail());
                return ResponseEntity.ok(Collections.singletonMap("token", token));
            } else {
                log.warn("Login failed for user: {}", request.getUsernameOrEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            log.error("Error during user login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        log.info("Logout request received");
        authService.logout(request);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        log.info("Password reset request for email: {}", email);
        boolean success = authService.requestPasswordReset(email);
        if (success) {
            return ResponseEntity.ok("Password reset email sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, 
                                               @RequestParam String newPassword) {
        log.info("Password reset with token");
        boolean success = authService.resetPassword(token, newPassword);
        if (success) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
    }
}

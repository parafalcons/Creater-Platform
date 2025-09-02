package com.platform.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("Processing request: {} {}", request.getMethod(), requestURI);

        // Skip JWT processing for authentication endpoints
        if (requestURI.startsWith("/auth/")) {
            log.debug("Skipping JWT processing for auth endpoint: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                // Extract user details from the token
                String username = jwtTokenProvider.getUsername(token);
                String mobileNumber = jwtTokenProvider.getUserId(token);
                String sessionId = jwtTokenProvider.getSessionId(token);

                log.debug("Valid token found for user: {}", mobileNumber);

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    mobileNumber, // Username (or user ID in this case)
                    "", // Password (if needed, use user.getPassword())
                    true, // Whether the account is enabled
                    true, // Account non-expired
                    true, // Credentials non-expired
                    true, // Account non-locked
                    new ArrayList<>()
                );

                if (userDetails != null) {
                    // Set authentication in the security context
                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                            userDetails,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set for user: {}", mobileNumber);
                }
            } catch (Exception e) {
                log.error("Error processing JWT token: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("No valid token found for request: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

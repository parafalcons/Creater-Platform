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

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Extract user details from the token
            String username = jwtTokenProvider.getUsername(token);
            String mobileNumber = jwtTokenProvider.getUserId(token);
            String sessionId = jwtTokenProvider.getSessionId(token);

            // Validate the session
            // boolean isSessionValid = sessionService.validateSession(mobileNumber, sessionId);

            // if (isSessionValid) {
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
                }
            // } else {
            //     // If the session is invalid, clear the context and send an error response
            //     SecurityContextHolder.clearContext();
            //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //     response.getWriter().write("Invalid or expired session");
            //     return;
            // }
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

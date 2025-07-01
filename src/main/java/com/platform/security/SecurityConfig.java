package com.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers("/api/auth/*").permitAll()
                .requestMatchers("/api/upload/*").permitAll()
                .requestMatchers("/uploads/*").permitAll()
                // .requestMatchers("/api/payment").permitAll()
                // .requestMatchers("/api/payment/*").permitAll()
                // .requestMatchers("/callback/*").permitAll()
                // .requestMatchers(new TransactionStatusRequestMatcher()).permitAll() // Custom Matcher

                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // public static class TransactionStatusRequestMatcher implements RequestMatcher {
    // private static final Pattern TRANSACTION_STATUS_PATTERN = 
    // Pattern.compile("^/callback/transaction_status/.*$");


    // @Override
    // public boolean matches(HttpServletRequest request) {
    //     String path = request.getRequestURI();
    //     String fullURL = request.getRequestURL().toString(); // Full URL

    //     System.out.println("Incoming request URI: " + path);
        
    //     boolean matches = TRANSACTION_STATUS_PATTERN.matcher(path).matches();
    //     System.out.println("Does it match? " + matches);
    
    //     return matches;
    // }
    // }    
    // @Bean
    // public AuthenticationManager
    // authenticationManager(AuthenticationConfiguration configuration) throws
    // Exception {
    // return configuration.getAuthenticationManager();
    // }
}

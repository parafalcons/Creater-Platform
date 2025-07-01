package com.platform.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    
    private String fullName;
    private String bio;
    private String email;
    private String phoneNumber;
    private String profilePhotoUrl;
} 
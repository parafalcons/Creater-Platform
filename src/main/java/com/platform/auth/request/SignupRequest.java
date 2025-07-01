package com.platform.auth.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String fullName;
    private String userName;
    private String phoneNumber;
    private String email;
    private String password;
}

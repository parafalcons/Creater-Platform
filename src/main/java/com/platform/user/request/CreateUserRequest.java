package com.platform.user.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    
    private String userName;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}

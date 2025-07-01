package com.platform.user.response;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String userName;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;

}

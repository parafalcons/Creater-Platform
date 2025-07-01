package com.platform.user.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserConnectionResponse {
    private String photo;
    private String username;
    private String name;
    private Long connectionId; 
    private boolean isFollowing;

}

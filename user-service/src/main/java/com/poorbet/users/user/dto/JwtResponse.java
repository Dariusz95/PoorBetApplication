package com.poorbet.users.user.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private List<String> roles;
    private List<String> permissions;
    private long expiresAt;

    public JwtResponse(String token, String username, List<String> roles, List<String> permissions, long expiresAt) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.expiresAt = expiresAt;
    }
}

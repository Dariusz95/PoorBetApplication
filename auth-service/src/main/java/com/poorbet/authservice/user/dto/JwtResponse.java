package com.poorbet.authservice.user.dto;

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
    private String refreshToken;
    private long refreshExpiresAt;

    public JwtResponse(String token, String username, List<String> roles, List<String> permissions, long expiresAt,
                        String refreshToken, long refreshExpiresAt) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.expiresAt = expiresAt;
        this.refreshToken = refreshToken;
        this.refreshExpiresAt = refreshExpiresAt;
    }
}

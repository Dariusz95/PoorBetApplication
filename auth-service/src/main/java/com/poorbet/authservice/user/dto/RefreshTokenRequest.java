package com.poorbet.authservice.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "error.refreshToken.required")
        String refreshToken
) {}

package com.poorbet.users.user.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String email,
        String role,
        LocalDateTime createdAt
) {}
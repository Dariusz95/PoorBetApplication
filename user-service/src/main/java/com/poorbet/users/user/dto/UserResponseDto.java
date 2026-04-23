package com.poorbet.users.user.dto;

import com.poorbet.users.user.model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        Role role,
        LocalDateTime createdAt
) {
}
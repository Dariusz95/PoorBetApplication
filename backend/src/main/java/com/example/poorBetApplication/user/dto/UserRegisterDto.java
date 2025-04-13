package com.example.poorBetApplication.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {}
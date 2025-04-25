package com.example.poorBetApplication.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @NotBlank(message = "error.email.required")
        @Email(message = "error.email.invalid")
        String email,

        @NotBlank(message = "error.password.required")
        @Size(min = 8, message = "error.password.length")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "error.password.complexity"
        )
        String password
) {}
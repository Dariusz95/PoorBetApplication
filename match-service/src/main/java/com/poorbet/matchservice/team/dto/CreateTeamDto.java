package com.poorbet.matchservice.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamDto(
        @NotBlank(message = "error.team.required")
        @Size(max = 50)
        String name
) { }

package com.poorbet.matchservice.team.dto;

import jakarta.validation.constraints.NotNull;

public record IncreaseTeamPowerDto(
        @NotNull(message = "error.team.power.required")
        PowerType powerType
) {
}

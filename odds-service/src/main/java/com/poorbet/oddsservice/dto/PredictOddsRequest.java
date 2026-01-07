package com.poorbet.oddsservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PredictOddsRequest(
    @NotNull(message = "Home team strength is required")
    @Min(value = 0, message = "Home team strength must be >= 0")
    Double homeTeamStrength,

    @NotNull(message = "Away team strength is required")
    @Min(value = 0, message = "Away team strength must be >= 0")
    Double awayTeamStrength
) {}

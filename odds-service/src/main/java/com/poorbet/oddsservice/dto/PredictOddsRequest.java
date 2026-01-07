package com.poorbet.oddsservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PredictOddsRequest(
    @NotNull(message = "Home team attack is required")
    @Min(value = 0, message = "Home team attack must be >= 0")
    int homeTeamAttack,

    @NotNull(message = "Home team defense is required")
    @Min(value = 0, message = "Home team defense must be >= 0")
    int homeTeamDefense,

    @NotNull(message = "Away team attack is required")
    @Min(value = 0, message = "Away team attack must be >= 0")
    int awayTeamAttack,

    @NotNull(message = "Away team defense is required")
    @Min(value = 0, message = "Away team defense must be >= 0")
    int awayTeamDefense
) {}

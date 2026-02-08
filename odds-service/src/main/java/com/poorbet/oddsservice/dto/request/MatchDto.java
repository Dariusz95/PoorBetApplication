package com.poorbet.oddsservice.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MatchDto(
        @NotNull
        UUID matchId,
        @Min(0)
        int homeTeamAttack,
        @Min(0)
        int homeTeamDefense,
        @Min(0)
        int awayTeamAttack,
        @Min(0)
        int awayTeamDefense
) {
}

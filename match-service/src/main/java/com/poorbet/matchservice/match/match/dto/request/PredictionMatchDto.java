package com.poorbet.matchservice.match.match.dto.request;

import java.util.UUID;

public record PredictionMatchDto(
        UUID matchId,
        int homeTeamAttack,
        int homeTeamDefense,
        int awayTeamAttack,
        int awayTeamDefense
) {
}

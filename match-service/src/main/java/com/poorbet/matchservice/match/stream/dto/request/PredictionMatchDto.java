package com.poorbet.matchservice.match.stream.dto.request;

import java.util.UUID;

public record PredictionMatchDto(
        UUID matchId,
        int homeTeamAttack,
        int homeTeamDefense,
        int awayTeamAttack,
        int awayTeamDefense
) {
}

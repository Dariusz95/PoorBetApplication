package com.poorbet.oddsservice.dto.request;


import java.util.UUID;

public record MatchDto(
        UUID matchId,
        int homeTeamAttack,
        int homeTeamDefense,
        int awayTeamAttack,
        int awayTeamDefense
) {
}

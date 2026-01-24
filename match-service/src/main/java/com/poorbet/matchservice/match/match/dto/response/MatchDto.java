package com.poorbet.matchservice.match.match.dto.response;

import java.util.UUID;

public record MatchDto(
        UUID matchId,
        UUID homeTeamId,
        UUID awayTeamId,
        OddsDto odds
) {
}

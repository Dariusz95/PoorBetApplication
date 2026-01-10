package com.poorbet.matchservice.match.stream.dto.response;

import java.util.UUID;

public record MatchDto(
        UUID matchId,
        UUID homeTeamId,
        UUID awayTeamId
) {
}

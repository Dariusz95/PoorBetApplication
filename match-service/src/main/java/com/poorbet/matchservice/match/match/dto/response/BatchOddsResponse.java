package com.poorbet.matchservice.match.match.dto.response;

import com.poorbet.matchservice.match.match.dto.WinProbabilityDto;

import java.util.UUID;

public record BatchOddsResponse(
        UUID matchId,
        WinProbabilityDto oddsResponse
) {
}

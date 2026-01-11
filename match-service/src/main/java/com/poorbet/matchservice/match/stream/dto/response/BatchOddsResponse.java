package com.poorbet.matchservice.match.stream.dto.response;

import com.poorbet.matchservice.match.stream.dto.WinProbabilityDto;

import java.util.UUID;

public record BatchOddsResponse(
        UUID matchId,
        WinProbabilityDto oddsResponse
) {
}

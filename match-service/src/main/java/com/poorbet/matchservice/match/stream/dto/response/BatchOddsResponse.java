package com.poorbet.matchservice.match.stream.dto.response;

import com.poorbet.matchservice.match.stream.dto.OddsDto;

import java.util.UUID;

public record BatchOddsResponse(
        UUID matchId,
        OddsDto oddsResponse
) {
}

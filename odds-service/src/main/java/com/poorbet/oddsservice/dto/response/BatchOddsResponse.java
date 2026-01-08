package com.poorbet.oddsservice.dto.response;

import com.poorbet.oddsservice.dto.OddsResponseDto;

import java.util.UUID;

public record BatchOddsResponse(
        UUID matchId,
        OddsResponseDto oddsResponse
) {
}

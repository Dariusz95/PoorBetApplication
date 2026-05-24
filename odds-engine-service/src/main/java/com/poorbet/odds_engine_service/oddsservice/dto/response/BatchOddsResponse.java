package com.poorbet.odds_engine_service.oddsservice.dto.response;

import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;

import java.util.UUID;

public record BatchOddsResponse(
        UUID matchId,
        OddsResponseDto oddsResponse
) {
}

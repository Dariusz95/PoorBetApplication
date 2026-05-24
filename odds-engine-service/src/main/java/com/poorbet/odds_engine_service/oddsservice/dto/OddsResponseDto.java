package com.poorbet.odds_engine_service.oddsservice.dto;

public record OddsResponseDto(
        float homeWinProbability,
        float drawProbability,
        float awayWinProbability
) {
}

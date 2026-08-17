package com.poorbet.odds_engine_service.oddsservice.dto;

public record OddsResponseDto(
        float homeWinProbability,
        float drawProbability,
        float awayWinProbability,
        float over2_5Probability,
        float under2_5Probability,
        float over3_5Probability,
        float under3_5Probability
) {
}

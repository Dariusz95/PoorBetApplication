package com.poorbet.oddsservice.dto;

public record OddsResponseDto(
        float homeWinProbability,
        float drawProbability,
        float awayWinProbability
) {
}

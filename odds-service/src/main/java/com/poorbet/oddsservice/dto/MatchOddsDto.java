package com.poorbet.oddsservice.dto;

import java.util.UUID;

public record MatchOddsDto(
        UUID matchId,
        UUID homeTeamId,
        UUID awayTeamId,
        double homeWinProbability,
        double drawProbability,
        double awayWinProbability
) {}

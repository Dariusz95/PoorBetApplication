package com.poorbet.matchservice.match.match.dto;

public record WinProbabilityDto(Double homeWinProbability,
                                Double drawProbability,
                                Double awayWinProbability,
                                Double over2_5Probability,
                                Double under2_5Probability,
                                Double over3_5Probability,
                                Double under3_5Probability
) {
}
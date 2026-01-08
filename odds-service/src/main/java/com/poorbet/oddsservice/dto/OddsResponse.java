package com.poorbet.oddsservice.dto;

import lombok.AllArgsConstructor;

public record OddsResponse(
        float winProbability,
        float drawProbability,
        float lossProbability
) {
//    public OddsResponse {
//        if (winProbability == null || drawProbability == null || lossProbability == null) {
//            throw new IllegalArgumentException("All probabilities must be non-null");
//        }
//    }
}

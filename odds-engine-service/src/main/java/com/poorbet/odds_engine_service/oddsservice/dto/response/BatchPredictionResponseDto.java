package com.poorbet.odds_engine_service.oddsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchPredictionResponseDto {
    private List<BatchOddsResponse> matches;
}

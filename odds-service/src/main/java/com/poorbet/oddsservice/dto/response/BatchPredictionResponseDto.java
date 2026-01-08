package com.poorbet.oddsservice.dto.response;

import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchPredictionResponseDto {
    private List<BatchOddsResponse> matches;
}

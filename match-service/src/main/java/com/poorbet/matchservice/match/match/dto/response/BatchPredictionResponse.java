package com.poorbet.matchservice.match.match.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchPredictionResponse {
    private List<BatchOddsResponse> matches;
}

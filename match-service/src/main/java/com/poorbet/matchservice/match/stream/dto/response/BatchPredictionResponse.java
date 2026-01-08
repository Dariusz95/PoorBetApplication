package com.poorbet.matchservice.match.stream.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchPredictionResponse {
    private List<BatchOddsResponse> matches;
}

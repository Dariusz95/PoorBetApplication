package com.poorbet.oddsservice.dto.request;

import java.util.List;

public record BatchPredictionRequest(
        List<MatchDto> matches
) {}
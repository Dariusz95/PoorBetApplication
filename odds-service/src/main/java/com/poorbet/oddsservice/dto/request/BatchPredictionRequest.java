package com.poorbet.oddsservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchPredictionRequest(
        @NotEmpty
        @Valid
        List<MatchDto> matches
) {
}
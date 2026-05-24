package com.poorbet.odds_engine_service.simulation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


public record SimulationBatchRequest(
        @NotNull
        @Valid
        @Size(max = 100000)
        List<SimulationRequestDto> matches
) {
}
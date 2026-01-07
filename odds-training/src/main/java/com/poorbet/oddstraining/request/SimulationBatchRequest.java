package com.poorbet.oddstraining.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SimulationBatchRequest(
        @NotNull
        @Valid
        List<SimulationRequestDto> matches
) {
}

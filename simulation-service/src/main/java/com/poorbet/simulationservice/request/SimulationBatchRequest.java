package com.poorbet.simulationservice.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


public record SimulationBatchRequest(
        @NotNull
        @Valid
        @Size(max = 1000)
        List<SimulationRequest> matches
        ) {
}
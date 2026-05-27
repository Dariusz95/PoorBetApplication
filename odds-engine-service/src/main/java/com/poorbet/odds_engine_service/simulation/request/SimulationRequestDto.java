package com.poorbet.odds_engine_service.simulation.request;

import com.poorbet.odds_engine_service.simulation.model.TeamPower;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SimulationRequestDto(
        @NotNull
        UUID matchId,

        @NotNull
        @Valid
        TeamPower home,

        @NotNull
        @Valid
        TeamPower away) {
}

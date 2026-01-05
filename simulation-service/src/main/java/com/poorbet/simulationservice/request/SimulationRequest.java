package com.poorbet.simulationservice.request;

import com.poorbet.simulationservice.dto.TeamStatsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SimulationRequest(

        @NotNull
        UUID matchId,

        @NotNull
        @Valid
        TeamStatsDto home,

        @NotNull
        @Valid
        TeamStatsDto away) {
}

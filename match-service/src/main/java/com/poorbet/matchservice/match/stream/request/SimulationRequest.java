package com.poorbet.matchservice.match.stream.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SimulationRequest(

        @NotNull
        UUID matchId,

        @NotNull
        @Valid
        SimulationTeamStats home,

        @NotNull
        @Valid
        SimulationTeamStats away) {
}

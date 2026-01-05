package com.poorbet.matchservice.match.stream.request;


import jakarta.validation.constraints.NotNull;

public record SimulationTeamStats(
        @NotNull
        int attackPower,
        @NotNull
        int defencePower
) {}

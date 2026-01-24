package com.poorbet.matchservice.match.match.dto.request;


import jakarta.validation.constraints.NotNull;

public record SimulationTeamStats(
        @NotNull
        int attackPower,
        @NotNull
        int defencePower
) {}

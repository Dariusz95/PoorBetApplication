package com.poorbet.oddstraining.request;

import com.poorbet.oddstraining.model.TeamPower;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SimulationRequestDto(
        UUID matchId,
        @NotNull TeamPower home,
        @NotNull TeamPower away
) {}


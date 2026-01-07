package com.poorbet.oddstraining.request;

import com.poorbet.oddstraining.model.TeamPower;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SimulationRequestDto(
        UUID matchId,
        @NotNull @Valid TeamPower home,
        @NotNull @Valid TeamPower away
) {}


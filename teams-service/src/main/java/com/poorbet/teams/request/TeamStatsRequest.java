package com.poorbet.teams.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record TeamStatsRequest(
        @NotEmpty
        List<UUID> teamIds
) {}

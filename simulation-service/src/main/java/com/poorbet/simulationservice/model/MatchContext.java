package com.poorbet.simulationservice.model;

import com.poorbet.simulationservice.dto.TeamStatsDto;

import java.util.UUID;

public record MatchContext(UUID matchId, TeamStatsDto home, TeamStatsDto away) {}

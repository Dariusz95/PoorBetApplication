package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;

import java.util.List;
import java.util.UUID;

public interface MatchPoolSimulationService {

    void startPoolSimulation(UUID poolId, List<TeamStatsDto> teamStats);
}

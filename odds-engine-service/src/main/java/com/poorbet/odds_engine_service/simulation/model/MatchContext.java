package com.poorbet.odds_engine_service.simulation.model;


import com.poorbet.odds_engine_service.simulation.dto.TeamStatsDto;
import com.poorbet.odds_engine_service.simulation.request.SimulationRequestDto;

import java.util.UUID;

public record MatchContext(
        UUID matchId,
        TeamStatsDto home,
        TeamStatsDto away
) {

    public MatchContext(SimulationRequestDto dto) {
        this(
                dto.matchId(),
                map(dto.home()),
                map(dto.away())
        );
    }

    public MatchContext(UUID matchId, SimulationRequestDto dto) {
        this(
                matchId,
                map(dto.home()),
                map(dto.away())
        );
    }


    private static TeamStatsDto map(TeamPower power) {
        return new TeamStatsDto(
                power.attackPower(),
                power.defencePower()
        );
    }
}
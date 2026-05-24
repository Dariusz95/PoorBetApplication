package com.poorbet.odds_engine_service.simulation.dto;


import com.poorbet.odds_engine_service.simulation.model.enums.MatchEventType;

import java.util.UUID;

public record LiveMatchEvent(
        UUID matchId,
        int minute,
        int homeGoals,
        int awayGoals,
        MatchEventType eventType,
        String eventData
) {
}

package com.poorbet.simulationservice.dto;

import com.poorbet.simulationservice.model.enums.MatchEventType;

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

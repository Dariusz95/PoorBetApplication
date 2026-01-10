package com.poorbet.oddstraining.dto;

import com.poorbet.oddstraining.dto.enums.MatchEventType;

import java.util.UUID;

public record LiveMatchEventDto(
        UUID matchId,
        int minute,
        int homeGoals,
        int awayGoals,
        MatchEventType eventType,
        String eventData
) {}
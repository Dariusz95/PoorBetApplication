package com.poorbet.oddstraining.dto;

import java.util.UUID;

public record LiveMatchEventDto(
        UUID matchId,
        int minute,
        int homeGoals,
        int awayGoals,
        boolean finished
) {}
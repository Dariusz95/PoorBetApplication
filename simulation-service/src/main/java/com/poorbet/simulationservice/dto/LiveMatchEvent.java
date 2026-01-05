package com.poorbet.simulationservice.dto;

import java.util.UUID;

public record LiveMatchEvent(
        UUID matchId,
        int minute,
        int homeGoals,
        int awayGoals,
        boolean finished
) {}
package com.poorbet.matchservice.match.match.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MatchBetSnapshotDto(
    UUID matchId,
    String homeTeamName,
    String awayTeamName,
    LocalDateTime matchStartTime,
    BigDecimal odd
) {}

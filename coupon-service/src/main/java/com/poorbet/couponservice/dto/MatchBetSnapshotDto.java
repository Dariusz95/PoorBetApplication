package com.poorbet.couponservice.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchBetSnapshotDto(
    UUID matchId,
    String homeTeamName,
    String awayTeamName,
    OffsetDateTime matchStartTime,
    BigDecimal odd
) {}

package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.BetType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BetDto(
        UUID id,
        UUID matchId,
        String homeTeamName,
        String awayTeamName,
        OffsetDateTime matchStartTime,
        BetStatus status,
        BetType betType,
        BigDecimal odds
) {
}


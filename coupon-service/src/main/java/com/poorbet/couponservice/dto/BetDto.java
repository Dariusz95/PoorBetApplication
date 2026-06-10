package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.BetType;

import java.math.BigDecimal;
import java.util.UUID;

public record BetDto(
        UUID id,
        UUID matchId,
        BetStatus status,
        BetType betType,
        BigDecimal odds
) {
}


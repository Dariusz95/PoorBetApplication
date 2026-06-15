package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.domain.CouponStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CouponDetailDto(
        UUID id,
        BigDecimal stake,
        CouponStatus status,
        BigDecimal potentialPayout,
        OffsetDateTime createdAt,
        List<BetDto> bets
) {}
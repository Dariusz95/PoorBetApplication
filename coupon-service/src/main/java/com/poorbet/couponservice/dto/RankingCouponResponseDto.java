package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.domain.CouponStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RankingCouponResponseDto(
        UUID couponId,
        BigDecimal stake,
        String email,
        CouponStatus status,
        BigDecimal potentialPayout,
        OffsetDateTime createdAt,
        BigDecimal totalOdds
) {}

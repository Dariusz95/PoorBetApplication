package com.poorbet.couponservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CouponLiveViewDto(
        UUID couponId,
        UUID matchId,
        BigDecimal totalOdds,
        BigDecimal potentialPayout
) {}

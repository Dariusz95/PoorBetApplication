package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.domain.CouponStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CouponDto(
        UUID id,
        BigDecimal stake,
        CouponStatus status,
        BigDecimal potentialPayout,
        OffsetDateTime createdAt
) {}
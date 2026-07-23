package com.poorbet.couponservice.dto;

import com.poorbet.commons.commons.pagination.PageResponse;

import java.time.OffsetDateTime;

public record RankingResponseDto(
        PageResponse<RankingCouponResponseDto> ranking,
        OffsetDateTime lastUpdatedAt
) {}

package com.poorbet.couponservice.projections;

import com.poorbet.couponservice.domain.BetType;

import java.util.UUID;

public interface BetProjection {
    UUID getId();
    UUID getMatchId();
    BetType getBetType();
}


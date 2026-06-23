package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.domain.BetType;

import java.util.UUID;

public record BetSnapshotRequest(UUID matchId, BetType betType) {}

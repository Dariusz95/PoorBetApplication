package com.poorbet.matchservice.match.match.dto;

import com.poorbet.matchservice.match.match.domain.OddsType;

import java.util.UUID;

public record BetSnapshotRequest(UUID matchId, OddsType betType) {}

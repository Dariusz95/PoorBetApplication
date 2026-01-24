package com.poorbet.matchservice.match.matchpool.service.helper;

import com.poorbet.matchservice.match.match.dto.TeamStatsDto;

import java.util.UUID;

public record MatchContext(UUID matchId, TeamStatsDto home, TeamStatsDto away) {}


package com.poorbet.matchservice.match.stream.service.helper;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;

import java.util.UUID;

public record MatchContext(UUID matchId, TeamStatsDto home, TeamStatsDto away) {}


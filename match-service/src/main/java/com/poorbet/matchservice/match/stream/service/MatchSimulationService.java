package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.service.helper.MatchContext;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MatchSimulationService {
    Flux<LiveMatchEvent> simulateMatchLive(MatchContext context);
}
package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.service.helper.MatchContext;
import reactor.core.publisher.Flux;

public interface MatchSimulationService {
    Flux<LiveMatchEvent> simulateMatchLive(MatchContext context);
}
package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.dto.SimulateMatchRequest;
import reactor.core.publisher.Flux;

public interface MatchSimulationService {
    Flux<LiveMatchEvent> simulateMatchLive(SimulateMatchRequest request);
}
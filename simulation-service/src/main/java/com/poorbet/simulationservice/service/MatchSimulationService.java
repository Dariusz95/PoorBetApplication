package com.poorbet.simulationservice.service;

import com.poorbet.simulationservice.dto.LiveMatchEvent;
import com.poorbet.simulationservice.model.MatchContext;
import reactor.core.publisher.Flux;

public interface MatchSimulationService {
    Flux<LiveMatchEvent> simulateMatchLive(MatchContext context);

}

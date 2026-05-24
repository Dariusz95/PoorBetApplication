package com.poorbet.odds_engine_service.simulation.service;

import com.poorbet.odds_engine_service.simulation.dto.LiveMatchEvent;
import com.poorbet.odds_engine_service.simulation.model.MatchContext;
import com.poorbet.odds_engine_service.simulation.request.SimulationBatchRequest;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MatchSimulationService {
    Flux<LiveMatchEvent> simulateMatchLive(MatchContext context);

    LiveMatchEvent simulateMatchInstant(MatchContext context);

    List<LiveMatchEvent> simulateBatch(SimulationBatchRequest request);
}

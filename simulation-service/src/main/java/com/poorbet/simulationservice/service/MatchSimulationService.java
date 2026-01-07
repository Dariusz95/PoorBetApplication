package com.poorbet.simulationservice.service;

import com.poorbet.simulationservice.dto.LiveMatchEvent;
import com.poorbet.simulationservice.model.MatchContext;
import com.poorbet.simulationservice.request.SimulationBatchRequest;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MatchSimulationService {
    Flux<LiveMatchEvent> simulateMatchLive(MatchContext context);

    LiveMatchEvent simulateMatchFinal(MatchContext context);
    List<LiveMatchEvent> simulateBatch(SimulationBatchRequest request);
}

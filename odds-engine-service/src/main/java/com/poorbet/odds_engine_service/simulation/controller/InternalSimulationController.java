package com.poorbet.odds_engine_service.simulation.controller;

import com.poorbet.odds_engine_service.simulation.dto.LiveMatchEvent;
import com.poorbet.odds_engine_service.simulation.model.MatchContext;
import com.poorbet.odds_engine_service.simulation.request.SimulationBatchRequest;
import com.poorbet.odds_engine_service.simulation.request.SimulationRequestDto;
import com.poorbet.odds_engine_service.simulation.service.MatchSimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/internal/simulation")
@RequiredArgsConstructor
public class InternalSimulationController {

    private final MatchSimulationService simulationService;

    @PostMapping(
            value = "/live",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<LiveMatchEvent> simulateMatch(
            @Valid @RequestBody SimulationRequestDto request
    ) {
        MatchContext context = new MatchContext(request);

        return simulationService.simulateMatchLive(context);
    }

    @PostMapping(
            value = "/instant"
    )
    public LiveMatchEvent simulateMatchInstant(
            @Valid @RequestBody SimulationRequestDto request
    ) {

        MatchContext context = new MatchContext(request);
        return simulationService.simulateMatchInstant(context);
    }

    @PostMapping(
            value = "/batch-result"
    )
    public List<LiveMatchEvent> simulateBatch(
            @Valid @RequestBody SimulationBatchRequest request
    ) {
        return simulationService.simulateBatch(request);
    }
}

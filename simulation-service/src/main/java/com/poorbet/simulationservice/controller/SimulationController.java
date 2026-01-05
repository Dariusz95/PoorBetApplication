package com.poorbet.simulationservice.controller;

import com.poorbet.simulationservice.dto.LiveMatchEvent;
import com.poorbet.simulationservice.model.MatchContext;
import com.poorbet.simulationservice.request.SimulationRequest;
import com.poorbet.simulationservice.service.MatchSimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final MatchSimulationService simulationService;

    @PostMapping(
            value = "/live",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<LiveMatchEvent> simulateMatch(
            @Valid @RequestBody SimulationRequest request
    ) {
        MatchContext context = new MatchContext(request.matchId(), request.home(), request.away());

        return simulationService.simulateMatchLive(context);
    }
}

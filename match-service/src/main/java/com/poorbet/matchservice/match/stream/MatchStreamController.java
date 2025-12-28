package com.poorbet.matchservice.match.stream;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.dto.SimulateMatchRequest;
import com.poorbet.matchservice.match.stream.service.MatchSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchStreamController {

    private final MatchSimulationService simulationService;

    @PostMapping(value = "/simulate/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LiveMatchEvent> simulateMatchLive(@RequestBody SimulateMatchRequest request) {
        return simulationService.simulateMatchLive(request);
    }
}
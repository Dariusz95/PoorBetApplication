package com.poorbet.matchservice.match.stream.client;

import com.poorbet.matchservice.match.stream.dto.request.SimulationRequest;
import com.poorbet.matchservice.match.stream.dto.response.LiveMatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class SimulationClient {
    private final WebClient simulationWebClient;

    public SimulationClient(@Qualifier("simulationWebClient") WebClient simulationWebClient) {
        this.simulationWebClient = simulationWebClient;
    }

    public Flux<LiveMatchEvent> simulateMatch(SimulationRequest request) {
        return simulationWebClient.post()
                .uri("/api/simulation/live")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(LiveMatchEvent.class);
    }
}

package com.poorbet.oddstraining.client;

import com.poorbet.oddstraining.request.SimulationBatchRequest;
import com.poorbet.oddstraining.dto.LiveMatchEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SimulationClient {

    private final WebClient simulationWebClient;

    public List<LiveMatchEventDto> simulateBatchMatch(SimulationBatchRequest request) {
        return simulationWebClient.post()
                .uri("/api/simulation/batch-result")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LiveMatchEventDto>>() {})
                .block();
    }
}

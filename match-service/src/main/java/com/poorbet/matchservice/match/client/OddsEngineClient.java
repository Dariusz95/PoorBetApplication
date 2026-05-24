package com.poorbet.matchservice.match.client;

import com.poorbet.matchservice.match.match.dto.request.PredictionBatchRequestDto;
import com.poorbet.matchservice.match.match.dto.request.SimulationRequest;
import com.poorbet.matchservice.match.match.dto.response.BatchPredictionResponse;
import com.poorbet.matchservice.match.match.dto.response.LiveMatchEvent;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;


@Slf4j
@Service
public class OddsEngineClient {
    private final WebClient oddsEngineWebClient;

    public OddsEngineClient(@Qualifier("oddsEngineWebClient") WebClient webClient) {
        this.oddsEngineWebClient = webClient;
    }

    public BatchPredictionResponse getBatchPrediction(@Valid PredictionBatchRequestDto data) {
        if (data == null || data.getMatches() == null || data.getMatches().isEmpty()) {
            log.warn("Skipping odds batch prediction request because match list is empty");
            return new BatchPredictionResponse(List.of());
        }

        return this.oddsEngineWebClient.post()
                .uri("/internal/odds/predict/batch")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BatchPredictionResponse>() {
                })
                .block();
    }

    public Flux<LiveMatchEvent> simulateMatch(SimulationRequest request) {
        return oddsEngineWebClient.post()
                .uri("/internal/simulation/live")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(LiveMatchEvent.class);
    }
}

package com.poorbet.matchservice.match.client;

import com.poorbet.matchservice.match.match.dto.request.PredictionBatchRequestDto;
import com.poorbet.matchservice.match.match.dto.request.SimulationRequest;
import com.poorbet.matchservice.match.match.dto.response.BatchPredictionResponse;
import com.poorbet.matchservice.match.match.dto.response.LiveMatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;


@Slf4j
@Service
public class OddsEngineClient {
    private final WebClient oddsEngineWebClient;
    private final RestClient oddsEngineRestClient;

    public OddsEngineClient(@Qualifier("oddsEngineWebClient") WebClient webClient,
                            @Qualifier("oddsEngineRestClient") RestClient restClient) {
        this.oddsEngineWebClient = webClient;
        this.oddsEngineRestClient = restClient;
    }

    @Retryable(value = IllegalStateException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public BatchPredictionResponse getBatchPrediction(PredictionBatchRequestDto data) {

        if (data == null || data.getMatches() == null || data.getMatches().isEmpty()) {
            log.warn("Skipping odds batch prediction request because match list is empty");
            return new BatchPredictionResponse(List.of());
        }

        try {
            return oddsEngineRestClient.post()
                    .uri("/internal/odds/predict/batch")
                    .body(data)
                    .retrieve()
                    .body(BatchPredictionResponse.class);

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().is5xxServerError()) {
                throw new IllegalStateException("Odds service not ready", ex);
            }

            throw ex;
        }
    }

    public Flux<LiveMatchEvent> simulateMatch(SimulationRequest request) {
        return oddsEngineWebClient.post()
                .uri("/internal/simulation/live")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(LiveMatchEvent.class);
    }
}

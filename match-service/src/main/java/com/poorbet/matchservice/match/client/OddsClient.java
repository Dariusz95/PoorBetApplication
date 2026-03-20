package com.poorbet.matchservice.match.client;

import com.poorbet.matchservice.match.match.dto.request.PredictionBatchRequestDto;
import com.poorbet.matchservice.match.match.dto.WinProbabilityDto;
import com.poorbet.matchservice.match.match.dto.request.PredictionRequestDto;
import com.poorbet.matchservice.match.match.dto.response.BatchPredictionResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Slf4j
@Service
public class OddsClient {
    private final WebClient oddsWebClient;

    public OddsClient(@Qualifier("oddsWebClient") WebClient webClient){
        this.oddsWebClient = webClient;
    }

    public WinProbabilityDto getPrediction(@Valid PredictionRequestDto data){
        return this.oddsWebClient.post()
                .uri("/internal/odds/predict")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<WinProbabilityDto>() {})
                .block();
    }

    public BatchPredictionResponse getBatchPrediction(@Valid PredictionBatchRequestDto data){
        if (data == null || data.getMatches() == null || data.getMatches().isEmpty()) {
            log.warn("Skipping odds batch prediction request because match list is empty");
            return new BatchPredictionResponse(List.of());
        }

        return this.oddsWebClient.post()
                .uri("/internal/odds/predict/batch")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BatchPredictionResponse>() {})
                .block();
    }
}

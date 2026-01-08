package com.poorbet.matchservice.match.stream.client;

import com.poorbet.matchservice.match.stream.dto.request.PredictionBatchRequestDto;
import com.poorbet.matchservice.match.stream.dto.OddsDto;
import com.poorbet.matchservice.match.stream.dto.request.PredictionRequestDto;
import com.poorbet.matchservice.match.stream.dto.response.BatchPredictionResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Service
public class OddsClient {
    private WebClient oddsWebClient;

    public OddsClient(@Qualifier("oddsWebClient") WebClient webClient){
        this.oddsWebClient = webClient;
    }

    public OddsDto getPrediction(@Valid PredictionRequestDto data){
        return this.oddsWebClient.post()
                .uri("api/odds/predict")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<OddsDto>() {})
                .block();
    }

    public BatchPredictionResponse getBatchPrediction(@Valid PredictionBatchRequestDto data){
        return this.oddsWebClient.post()
                .uri("api/odds/predict/batch")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BatchPredictionResponse>() {})
                .block();
    }
}

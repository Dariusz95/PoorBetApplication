package com.poorbet.couponservice.client;

import com.poorbet.couponservice.dto.MatchResultMapDto;
import com.poorbet.couponservice.domain.OddsType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MatchClient {

    private final WebClient matchServiceWebClientBuilder;

    public MatchResultMapDto getMatchResult(List<UUID> matchIds) {
        return matchServiceWebClientBuilder
                .post()
                .uri("/api/match/results")
                .bodyValue(matchIds)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ServiceUnavailableException())
                )
                .bodyToMono(MatchResultMapDto.class)
                .block();
    }

    public Double getOdd(UUID matchId, OddsType type) {
        return matchServiceWebClientBuilder
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/match/{matchId}/odds")
                        .queryParam("type", type)
                        .build(matchId))
                .retrieve()
                .bodyToMono(Double.class)
                .block();
    }
}
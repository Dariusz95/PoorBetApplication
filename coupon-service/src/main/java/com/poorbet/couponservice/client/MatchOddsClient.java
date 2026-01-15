package com.poorbet.couponservice.client;

import com.poorbet.couponservice.model.enums.OddsType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MatchClient {

    private final WebClient matchClient;

    public Double getOdd(UUID matchId, OddsType type) {
        return matchClient
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

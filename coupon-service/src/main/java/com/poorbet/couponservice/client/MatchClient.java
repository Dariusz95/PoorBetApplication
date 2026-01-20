package com.poorbet.couponservice.client;

import com.poorbet.couponservice.dto.MatchResultMapDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MatchClient {

    private final WebClient matchServiceWebClientBuilder;

    public MatchResultMapDto getMatchResult(List<UUID> matchId) {
        return matchServiceWebClientBuilder
                .get()
                .uri("/api/match/{matchId}/result", matchId)
                .retrieve()
                .bodyToMono(MatchResultMapDto.class)
                .block();
    }
}
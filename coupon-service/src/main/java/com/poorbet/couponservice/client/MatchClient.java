package com.poorbet.couponservice.client;

import com.poorbet.couponservice.dto.MatchResultMapDto;
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
                .uri("/api/match/result")
                .bodyValue(matchIds)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ServiceUnavailableException())
                )
                .bodyToMono(MatchResultMapDto.class)
                .block();
    }
}
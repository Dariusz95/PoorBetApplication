package com.poorbet.couponservice.client.match;

import com.poorbet.couponservice.domain.BetType;
import com.poorbet.couponservice.dto.BetSnapshotRequest;
import com.poorbet.couponservice.dto.MatchBetSnapshotDto;
import com.poorbet.couponservice.dto.MatchResultMapDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
public class MatchClient {

    private final RestClient restClient;

    public MatchClient(@Qualifier("matchRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public MatchResultMapDto getMatchResult(List<UUID> matchIds) {
        return restClient
                .post()
                .uri("/internal/match/results")
                .body(matchIds)
                .retrieve()
                .body(MatchResultMapDto.class);
    }

    public MatchBetSnapshotDto getBetSnapshot(UUID matchId, BetType type) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/match/{matchId}/bet-snapshot")
                        .queryParam("type", type)
                        .build(matchId))
                .retrieve()
                .body(MatchBetSnapshotDto.class);
    }

    public List<MatchBetSnapshotDto> getBetSnapshots(List<BetSnapshotRequest> requests) {
        return restClient
                .post()
                .uri("/internal/match/bet-snapshots/batch")
                .body(requests)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
package com.poorbet.matchservice.match.stream.client;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamsClient {
    private final WebClient teamsWebClient;

    public TeamStatsDto getTeamStats(UUID id) {
        return teamsWebClient.get()
                .uri("/api/teams/{id}/stats", id)
                .retrieve()
                .bodyToMono(TeamStatsDto.class)
                .block();
    }
}

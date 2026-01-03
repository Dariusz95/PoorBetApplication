package com.poorbet.matchservice.match.stream.client;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamsClient {
    private final WebClient teamsWebClient;

    public List<TeamStatsDto> randomTeams() {
        return teamsWebClient.get()
                .uri("/api/teams/random")
                .retrieve()
                .bodyToFlux(TeamStatsDto.class)
                .collectList()
                .block();
    }
}

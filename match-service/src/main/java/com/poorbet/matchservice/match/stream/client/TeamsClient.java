package com.poorbet.matchservice.match.stream.client;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamsClient {
    private final WebClient teamsWebClient;

    public List<TeamStatsDto> randomTeams() {
        ParameterizedTypeReference<List<TeamStatsDto>> typeRef = new ParameterizedTypeReference<List<TeamStatsDto>>() {};

        try {
            return teamsWebClient.get()
                    .uri("/api/teams/random")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TeamStatsDto>>() {})
                    .block();

        } catch (WebClientResponseException ex) {
            log.error("Teams service returned {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("Cannot fetch random teams", ex);
            return Collections.emptyList();
        }
    }
}

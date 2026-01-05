package com.poorbet.matchservice.match.stream.client;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.request.TeamStatsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TeamsClient {
    private final WebClient teamsWebClient;

    public TeamsClient(@Qualifier("teamsWebClient") WebClient teamsWebClient) {
        this.teamsWebClient = teamsWebClient;
    }

    public List<TeamStatsDto> getStatsByIds(List<UUID> teamIds) {
        return teamsWebClient.post()
                .uri("/api/teams/stats")
                .bodyValue(new TeamStatsRequest(teamIds))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TeamStatsDto>>() {})
                .block();
    }

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

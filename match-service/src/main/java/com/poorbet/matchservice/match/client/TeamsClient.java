package com.poorbet.matchservice.match.client;

import com.poorbet.matchservice.match.match.dto.TeamStatsDto;
import com.poorbet.matchservice.match.match.dto.request.TeamStatsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TeamsClient {

    private final RestClient restClient;

    public TeamsClient(@Qualifier("teamsInternalRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<TeamStatsDto> getStatsByIds(List<UUID> teamIds) {

        try {
            return restClient.post()
                    .uri("/internal/teams/stats")
                    .header("X-Skip-Auth", "true")
                    .body(new TeamStatsRequest(teamIds))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TeamStatsDto>>() {});
        } catch (Exception ex) {
            log.error("Cannot fetch stats", ex);
            return Collections.emptyList();
        }
    }

    public List<TeamStatsDto> randomTeams(int count) {

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/internal/teams/random")
                            .queryParam("count", count)
                            .build()
                    )
                    .header("X-Skip-Auth", "true")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TeamStatsDto>>() {});

        } catch (Exception ex) {
            log.error("Cannot fetch random teams", ex);
            return Collections.emptyList();
        }
    }
}

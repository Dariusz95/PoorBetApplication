package com.poorbet.teams.controller;

import ch.qos.logback.core.joran.sanity.Pair;
import com.poorbet.teams.config.MatchesProperties;
import com.poorbet.teams.dto.MatchDto;
import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;
import com.poorbet.teams.request.TeamStatsRequest;
import com.poorbet.teams.service.TeamService;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final MatchesProperties matchesProperties;

    @PostMapping("/stats")
    public List<TeamStatsDto> getTeamStats(
            @RequestBody TeamStatsRequest request
    ) {
        return teamService.getStatsByIds(request.teamIds());
    }

    @GetMapping("/random")
    public ResponseEntity<List<TeamStatsDto>> getRandomTeams(
            @RequestParam(required = false) Integer count
    ) {
        int matchesInBatch = Optional.ofNullable(count)
                .orElse(matchesProperties.getInBatch());

        List<TeamStatsDto> teams = teamService.findRandomTeams(matchesInBatch);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public TeamShortDto getTeam(@PathVariable UUID id) {
        log.info("GET /api/teams/{} called", id);
        TeamShortDto team = teamService.getById(id);
        log.info("Returning team: id={}, name={}", team.id(), team.name());
        return team;
    }
}

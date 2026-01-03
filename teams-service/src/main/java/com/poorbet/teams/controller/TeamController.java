package com.poorbet.teams.controller;

import ch.qos.logback.core.joran.sanity.Pair;
import com.poorbet.teams.config.MatchesProperties;
import com.poorbet.teams.dto.MatchDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;
import com.poorbet.teams.service.TeamService;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final MatchesProperties matchesProperties;



    @GetMapping("/random")
    public ResponseEntity<List<TeamStatsDto>> getRandomTeams(
            @RequestParam(defaultValue = "2") Integer count
    ) {
        int matchesInBatch = (count != null) ? count : matchesProperties.getInBatch();

        List<TeamStatsDto> teams = teamService.findRandomTeams(matchesInBatch);
        return ResponseEntity.ok(teams);
    }
}

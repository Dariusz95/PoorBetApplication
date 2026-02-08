package com.poorbet.teams.controller;

import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.request.TeamStatsRequest;
import com.poorbet.teams.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/stats")
    public ResponseEntity<List<TeamStatsDto>> getTeamStats(
            @Valid @RequestBody TeamStatsRequest request
    ) {
        List<TeamStatsDto> result = teamService.getStatsByIds(request.teamIds());

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/random")
    public ResponseEntity<List<TeamStatsDto>> getRandomTeams(
            @RequestParam(required = false, defaultValue = "1") @Min(1) Integer count
    ) {
        if (count <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<TeamStatsDto> teams = teamService.findRandomTeams(count);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public TeamShortDto getTeam(@PathVariable UUID id) {
        TeamShortDto team = teamService.getById(id);
        log.info("Returning team: id={}, name={}", team.id(), team.name());

        return team;
    }
}

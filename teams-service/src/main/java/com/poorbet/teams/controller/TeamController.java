package com.poorbet.teams.controller;

import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;
import com.poorbet.teams.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @GetMapping("/{id}/stats")
    public ResponseEntity<TeamStatsDto> get(@PathVariable UUID id) {
        TeamStatsDto team = service.getStats(id);

        return ResponseEntity.ok(team);
    }
}

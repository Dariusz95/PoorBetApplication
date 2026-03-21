package com.poorbet.teams.controller;

import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/teams/public")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/{id}")
    public TeamShortDto getTeam(@PathVariable UUID id) {
        TeamShortDto team = teamService.getById(id);
        log.info("Returning team: id={}, name={}", team.id(), team.name());

        return team;
    }
}

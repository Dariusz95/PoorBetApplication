package com.poorbet.matchservice.team.controller;

import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamShortDto> uploadLogo(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(teamService.updateLogo(id, file));
    }
}

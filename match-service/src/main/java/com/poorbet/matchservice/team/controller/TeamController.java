package com.poorbet.matchservice.team.controller;

import com.poorbet.matchservice.security.CurrentUserProvider;
import com.poorbet.matchservice.team.dto.CreateTeamDto;
import com.poorbet.matchservice.team.dto.TeamResponse;
import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/{id}")
    public TeamShortDto getTeam(@PathVariable UUID id) {
        TeamShortDto team = teamService.getById(id);
        log.info("Returning team: id={}, name={}", team.id(), team.name());

        return team;
    }

    @PatchMapping(value = "/me/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TeamShortDto> uploadLogo(
            @RequestParam("file") MultipartFile file) {

        UUID userId = currentUserProvider.getUserId();

        return ResponseEntity.ok(teamService.updateLogo(userId, file));
    }

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamDto dto) {
        log.info("Create team with request={}", dto.toString());
        UUID userId = currentUserProvider.getUserId();

        TeamResponse response = teamService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

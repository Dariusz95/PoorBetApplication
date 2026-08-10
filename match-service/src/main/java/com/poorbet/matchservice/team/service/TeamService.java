package com.poorbet.matchservice.team.service;

import com.poorbet.matchservice.team.dto.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<TeamStatsDto> findRandomTeams(Integer count);

    List<TeamStatsDto> getStatsByIds(List<UUID> ids);

    TeamShortDto getById(UUID id);

    TeamResponse updateLogo(UUID userId, MultipartFile file);

    TeamResponse create(CreateTeamDto dto, UUID userId);

    TeamResponse update(@Valid UpdateTeamDto dto, UUID userId);

    TeamResponse increasePower(IncreaseTeamPowerDto dto, UUID userId);

    TeamResponse getMyTeam(UUID userId);
}

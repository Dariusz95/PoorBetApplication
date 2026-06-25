package com.poorbet.matchservice.team.service;

import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.dto.TeamStatsDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<TeamStatsDto> findRandomTeams(Integer count);
    List<TeamStatsDto> getStatsByIds(List<UUID> ids);
    TeamShortDto getById(UUID id);
    TeamShortDto updateLogo(UUID id, MultipartFile file);
}

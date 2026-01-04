package com.poorbet.teams.service;

import com.poorbet.teams.dto.TeamStatsDto;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<TeamStatsDto> findRandomTeams(Integer count);
    List<TeamStatsDto> getStatsByIds(List<UUID> ids);
}

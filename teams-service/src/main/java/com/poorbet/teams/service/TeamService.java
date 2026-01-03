package com.poorbet.teams.service;

import com.poorbet.teams.dto.MatchDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<TeamStatsDto> findRandomTeams(Integer count);
}

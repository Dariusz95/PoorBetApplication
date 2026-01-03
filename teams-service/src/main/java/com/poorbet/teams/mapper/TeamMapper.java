package com.poorbet.teams.mapper;

import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    public TeamStatsDto toDto(Team team){
        return new TeamStatsDto(
                team.getId(),
                team.getName(),
                team.getAttackPower(),
                team.getDefencePower()
        );
    }
}

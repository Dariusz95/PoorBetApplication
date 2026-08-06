package com.poorbet.matchservice.team.mapper;

import com.poorbet.matchservice.team.dto.TeamResponse;
import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.dto.TeamStatsDto;
import com.poorbet.matchservice.team.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    public static TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .attackPower(team.getAttackPower())
                .defencePower(team.getDefencePower())
                .build();
    }

    public TeamStatsDto toDto(Team team) {
        return new TeamStatsDto(
                team.getId(),
                team.getName(),
                team.getAttackPower(),
                team.getDefencePower()
        );
    }

    public static TeamShortDto toTeamShortDto(Team team) {
        return new TeamShortDto(
                team.getId(),
                team.getName(),
                toImgUrl(team.getImg())
        );
    }

    private static String toImgUrl(String img) {
        return img != null ? "/images/" + img : null;
    }
}

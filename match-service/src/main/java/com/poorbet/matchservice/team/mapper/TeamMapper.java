package com.poorbet.matchservice.team.mapper;

import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.dto.TeamStatsDto;
import com.poorbet.matchservice.team.model.Team;
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

    public static TeamShortDto toTeamShortDto(Team team){
        return new TeamShortDto(
                team.getId(),
                team.getName(),
                toImgUrl(team.getImg())
        );
    }

    // img w bazie to sama nazwa pliku (np. "ac-pierogi.png") — prefiks ścieżki serwującej
    // pliki statyczne (StaticResourceConfig) doklejamy tu, w jednym miejscu, przy budowaniu DTO
    private static String toImgUrl(String img) {
        return img != null ? "/images/" + img : null;
    }
}

package com.poorbet.matchservice.team.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchDto {
    TeamStatsDto home;
    TeamStatsDto away;
}

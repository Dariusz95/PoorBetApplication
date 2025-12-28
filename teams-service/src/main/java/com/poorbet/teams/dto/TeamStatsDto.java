package com.poorbet.teams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamStatsDto {
    private UUID id;
    private String name;
    private int attackPower;
    private int defencePower;
}

package com.poorbet.matchservice.match.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamStatsDto {
    private UUID id;
    private String name;
    private int attackPower;
    private int defencePower;
}

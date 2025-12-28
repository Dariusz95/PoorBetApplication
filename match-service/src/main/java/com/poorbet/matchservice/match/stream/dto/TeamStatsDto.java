package com.poorbet.matchservice.match.stream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatsDto {
    private UUID id;
    private int attack;
    private int defence;
}

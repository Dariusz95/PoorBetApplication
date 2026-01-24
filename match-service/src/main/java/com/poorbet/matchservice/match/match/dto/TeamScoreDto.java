package com.poorbet.matchservice.match.match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamScoreDto {
    private UUID teamId;
    private String teamName;
    private int score;
}

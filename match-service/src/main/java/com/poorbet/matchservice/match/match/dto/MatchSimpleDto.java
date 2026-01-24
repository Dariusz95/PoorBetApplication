package com.poorbet.matchservice.match.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchSimpleDto {
    private UUID matchId;
    private UUID homeTeamId;
    private UUID awayTeamId;
}
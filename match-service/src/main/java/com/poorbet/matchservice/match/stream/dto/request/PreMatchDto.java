package com.poorbet.matchservice.match.stream.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PreMatchDto {
    private UUID matchId;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private int homeAttack;
    private int homeDefense;
    private int awayAttack;
    private int awayDefense;
}
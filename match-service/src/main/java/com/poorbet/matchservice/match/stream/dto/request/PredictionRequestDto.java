package com.poorbet.matchservice.match.stream.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredictionRequestDto {
    int homeTeamAttack;
    int homeTeamDefense;
    int awayTeamAttack;
    int awayTeamDefense;
}

package com.poorbet.matchservice.match.match.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredictionRequestDto {
    int homeAttack;
    int homeDefense;
    int awayAttack;
    int awayDefense;
}

package com.poorbet.odds_engine_service.oddsservice.model;

import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;

public interface OddsModel {

    OddsResponseDto predict(
            int homeAttack,
            int homeDefense,
            int awayAttack,
            int awayDefense
    );
}
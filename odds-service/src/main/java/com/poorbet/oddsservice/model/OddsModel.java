package com.poorbet.oddsservice.model;

import com.poorbet.oddsservice.dto.OddsResponseDto;

public interface OddsModel {

    OddsResponseDto predict(
            int homeAttack,
            int homeDefense,
            int awayAttack,
            int awayDefense
    );
}
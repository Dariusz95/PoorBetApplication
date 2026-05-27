package com.poorbet.odds_engine_service.oddsservice.service;

import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;
import com.poorbet.odds_engine_service.oddsservice.dto.request.MatchDto;
import com.poorbet.odds_engine_service.oddsservice.dto.response.BatchOddsResponse;

import java.util.List;

public interface OddsService {
    OddsResponseDto predictOdds(
            int homeAttack,
            int homeDefense,
            int awayAttack,
            int awayDefense
    );

    List<BatchOddsResponse> predictBatch(List<MatchDto> matches);
}
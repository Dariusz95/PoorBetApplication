package com.poorbet.oddsservice.service;

import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.request.MatchDto;

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
package com.poorbet.odds_engine_service.oddsservice.service;

import com.poorbet.odds_engine_service.ml.SmileOddsModel;
import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;
import com.poorbet.odds_engine_service.oddsservice.dto.request.MatchDto;
import com.poorbet.odds_engine_service.oddsservice.dto.response.BatchOddsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OddsServiceImpl implements OddsService {

    private final SmileOddsModel oddsModel;

    @Override
    public OddsResponseDto predictOdds(
            int homeAttack,
            int homeDefense,
            int awayAttack,
            int awayDefense
    ) {
        return oddsModel.predict(
                homeAttack,
                homeDefense,
                awayAttack,
                awayDefense
        );
    }

    @Override
    public List<BatchOddsResponse> predictBatch(List<MatchDto> matches) {
        return matches.stream()
                .map(match -> {
                    OddsResponseDto odds = oddsModel.predict(
                            match.homeTeamAttack(),
                            match.homeTeamDefense(),
                            match.awayTeamAttack(),
                            match.awayTeamDefense()
                    );
                    return new BatchOddsResponse(match.matchId(), odds);
                })
                .toList();
    }
}


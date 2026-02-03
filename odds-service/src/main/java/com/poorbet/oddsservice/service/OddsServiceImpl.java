package com.poorbet.oddsservice.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.request.MatchDto;
import com.poorbet.oddsservice.model.OddsModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OddsServiceImpl implements OddsService {

    private final OddsModel oddsModel;

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


package com.poorbet.oddsservice.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;


import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.request.MatchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class OddsServiceImpl implements OddsService {

    private final OrtEnvironment ortEnvironment;
    private final OrtSession session;

    public OddsServiceImpl(@Value("${model.path:/app/data/models/match_predictor.onnx}") String modelPath) {
        try {
            this.ortEnvironment = OrtEnvironment.getEnvironment();
            this.session = ortEnvironment.createSession(modelPath, new OrtSession.SessionOptions());
            log.info("ONNX model loaded from: {}", modelPath);
        } catch (OrtException e) {
            log.error("Failed to load ONNX model from: {}", modelPath, e);
            throw new RuntimeException("Failed to initialize ONNX model", e);
        }
    }

    public OddsResponseDto predictOdds(
            int homeAttack,
            int homeDefence,
            int awayAttack,
            int awayDefence
    ) {
        float[][] input = new float[][]{{
                homeAttack,
                homeDefence,
                awayAttack,
                awayDefence
        }};

        try (OnnxTensor tensor = OnnxTensor.createTensor(ortEnvironment, input);
             OrtSession.Result result = session.run(
                     Collections.singletonMap("float_input", tensor)
             )) {

            float[] probs = ((float[][]) result.get(1).getValue())[0];

            return new OddsResponseDto(
                    probs[0], // H
                    probs[1], // X
                    probs[2]  // A
            );

        } catch (OrtException e) {
            throw new RuntimeException("ONNX inference failed", e);
        }
    }

    @Override
    public List<BatchOddsResponse> predictBatch(List<MatchDto> matches) {
        List<BatchOddsResponse> oddsList = matches.stream()
                .map(match -> {
                    OddsResponseDto odds = predictOdds(
                            match.homeTeamAttack(),
                            match.homeTeamDefense(),
                            match.awayTeamAttack(),
                            match.awayTeamDefense()
                    );

                    log.info("odds ->  {}", odds);

                    return new BatchOddsResponse(
                            match.matchId(),
                            odds
                    );
                })
                .toList();


        return oddsList;
    }
}


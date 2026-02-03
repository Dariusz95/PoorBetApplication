package com.poorbet.oddsservice.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.model.OddsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class OnnxOddsModel implements OddsModel {

    private final OrtEnvironment ortEnvironment;
    private final OrtSession session;

    public OnnxOddsModel(
            @Value("${model.path:/app/data/models/match_predictor.onnx}") String modelPath
    ) {
        try {
            this.ortEnvironment = OrtEnvironment.getEnvironment();
            this.session = ortEnvironment.createSession(modelPath, new OrtSession.SessionOptions());
            log.info("ONNX model loaded from: {}", modelPath);
        } catch (OrtException e) {
            log.error("Failed to load ONNX model from: {}", modelPath, e);
            throw new IllegalStateException("ONNX model initialization failed", e);
        }
    }

    @Override
    public OddsResponseDto predict(
            int homeAttack,
            int homeDefense,
            int awayAttack,
            int awayDefense
    ) {
        float[][] input = new float[][]{{
                homeAttack,
                homeDefense,
                awayAttack,
                awayDefense
        }};

        try (OnnxTensor tensor = OnnxTensor.createTensor(ortEnvironment, input);
             OrtSession.Result result = session.run(
                     Collections.singletonMap("float_input", tensor))
        ) {
            float[] probs = ((float[][]) result.get(1).getValue())[0];
            return new OddsResponseDto(probs[0], probs[1], probs[2]);

        } catch (OrtException e) {
            throw new RuntimeException("ONNX inference failed", e);
        }
    }
}
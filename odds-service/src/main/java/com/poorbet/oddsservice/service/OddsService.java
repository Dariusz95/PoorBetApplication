package com.poorbet.oddsservice.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.poorbet.oddsservice.dto.OddsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class OddsService {


    private final OrtEnvironment ortEnvironment;
    private final OrtSession session;

    public OddsService(@Value("${model.path:/app/data/models/match_predictor.onnx}") String modelPath) {
        try {
            this.ortEnvironment = OrtEnvironment.getEnvironment();
            this.session = ortEnvironment.createSession(modelPath, new OrtSession.SessionOptions());
            log.info("ONNX model loaded from: {}", modelPath);
        } catch (OrtException e) {
            log.error("Failed to load ONNX model from: {}", modelPath, e);
            throw new RuntimeException("Failed to initialize ONNX model", e);
        }
    }


    public OddsResponse predictOdds(
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

            return new OddsResponse(
                    probs[0], // H
                    probs[1], // X
                    probs[2]  // A
            );

        } catch (OrtException e) {
            throw new RuntimeException("ONNX inference failed", e);
        }
    }
}

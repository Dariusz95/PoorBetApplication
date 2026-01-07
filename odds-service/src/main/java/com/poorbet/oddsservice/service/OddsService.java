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
import java.util.Map;

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

    public OddsResponse predictOdds(int homeTeamAttack, int homeTeamDefense, int awayTeamAttack, int awayTeamDefense) {
        float[][] inputData = {{
                (float) homeTeamAttack,
                (float) homeTeamDefense,
                (float) awayTeamAttack,
                (float) awayTeamDefense
        }};

        try (OnnxTensor tensor = OnnxTensor.createTensor(ortEnvironment, inputData);
             OrtSession.Result outputs = session.run(Collections.singletonMap("float_input", tensor))) {

//            float[] probabilities = ((float[][]) outputs.get(0).getValue())[0];

            float[] probabilities;
            Object rawOutput = outputs.get(0).getValue();
            log.info("aaaaaaaaaaaaaa {}", rawOutput);
            if (rawOutput instanceof float[][] floatArray2D) {
                probabilities = floatArray2D[0];
            } else if (rawOutput instanceof double[][] doubleArray2D) {
                probabilities = new float[doubleArray2D[0].length];
                for (int i = 0; i < doubleArray2D[0].length; i++) {
                    probabilities[i] = (float) doubleArray2D[0][i];
                }
            } else if (rawOutput instanceof long[][] longArray2D) {
                probabilities = new float[longArray2D[0].length];
                for (int i = 0; i < longArray2D[0].length; i++) {
                    probabilities[i] = longArray2D[0][i];
                }
            } else {
                throw new RuntimeException("Unexpected ONNX output type: " + rawOutput.getClass());
            }


            if (probabilities.length < 3) {
                throw new RuntimeException("Model output does not contain 3 probabilities");
            }

            return new OddsResponse(
                    probabilities[0],  // Win probability
                    probabilities[1],  // Draw probability
                    probabilities[2]   // Loss probability
            );

        } catch (OrtException e) {
            log.error("Error during ONNX model inference", e);
            throw new RuntimeException("Failed to predict odds", e);
        }
    }
}

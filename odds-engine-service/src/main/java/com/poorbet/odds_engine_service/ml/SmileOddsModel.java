package com.poorbet.odds_engine_service.ml;

import com.poorbet.odds_engine_service.config.ModelProperties;
import com.poorbet.odds_engine_service.ml.model.OddsModel;
import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmileOddsModel implements OddsModel {

    private TrainedModelBundle models;
    private final ModelProperties modelProperties;

    public void loadModel() {

        Path path = modelProperties.getPath();

        if (!Files.exists(path)) {
            log.warn("Model file not found: {}", path);
            return;
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(
                             Files.newInputStream(path)
                     )) {

            this.models = (TrainedModelBundle) ois.readObject();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot load ML model",
                    e
            );
        }
    }

    @Override
    public OddsResponseDto predict(
            int homeAttack,
            int homeDefense,
            int awayAttack,
            int awayDefense
    ) {

        if (models == null) {
            throw new IllegalStateException("Model not loaded");
        }

        double[] features = new double[]{
                homeAttack,
                homeDefense,
                awayAttack,
                awayDefense
        };

        double[] resultProbs = new double[3];
        models.matchResultModel().predict(features, resultProbs);

        double[] over2_5Probs = new double[2];
        models.over2_5Model().predict(features, over2_5Probs);

        double[] over3_5Probs = new double[2];
        models.over3_5Model().predict(features, over3_5Probs);

        return new OddsResponseDto(
                (float) resultProbs[0],
                (float) resultProbs[1],
                (float) resultProbs[2],
                (float) over2_5Probs[1],
                (float) over2_5Probs[0],
                (float) over3_5Probs[1],
                (float) over3_5Probs[0]
        );
    }
}
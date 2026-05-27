package com.poorbet.odds_engine_service.ml;

import com.poorbet.odds_engine_service.config.ModelProperties;
import com.poorbet.odds_engine_service.ml.model.OddsModel;
import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smile.classification.LogisticRegression;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmileOddsModel implements OddsModel {

    private LogisticRegression model;
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

            this.model = (LogisticRegression) ois.readObject();

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

        if (model == null) {
            throw new IllegalStateException("Model not loaded");
        }

        double[] features = new double[]{
                homeAttack,
                homeDefense,
                awayAttack,
                awayDefense
        };

        double[] probs = new double[3];

        model.predict(features, probs);

        return new OddsResponseDto(
                (float) probs[0],
                (float) probs[1],
                (float) probs[2]
        );
    }
}
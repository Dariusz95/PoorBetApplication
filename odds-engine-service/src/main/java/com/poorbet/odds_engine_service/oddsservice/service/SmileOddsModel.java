package com.poorbet.odds_engine_service.oddsservice.service;

import com.poorbet.odds_engine_service.bootstrap.SystemState;
import com.poorbet.odds_engine_service.config.ModelProperties;
import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;
import com.poorbet.odds_engine_service.oddsservice.model.OddsModel;
import jakarta.annotation.PostConstruct;
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
    private final SystemState modelState;
    private final ModelProperties modelProperties;

    @PostConstruct
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

            modelState.markReady();

        } catch (Exception e) {

            log.error("Failed to load model", e);

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
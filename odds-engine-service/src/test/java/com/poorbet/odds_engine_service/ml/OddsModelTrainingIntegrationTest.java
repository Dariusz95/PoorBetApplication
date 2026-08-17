package com.poorbet.odds_engine_service.ml;

import com.poorbet.odds_engine_service.dataset.CsvDatasetService;
import com.poorbet.odds_engine_service.oddsservice.dto.OddsResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class OddsModelTrainingIntegrationTest {

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void overridePaths(DynamicPropertyRegistry registry) {
        registry.add("dataset.output-path", () -> tempDir.resolve("matches.csv").toString());
        registry.add("model.path", () -> tempDir.resolve("model.ser").toString());
    }

    @Autowired
    private CsvDatasetService csvDatasetService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private SmileOddsModel smileOddsModel;

    @Test
    void shouldTrainAndServeGoalMarketOddsAlongside1X2() {
        // A full round trip through the real pipeline: generate a training CSV
        // (with the home_goals/away_goals columns), train the 1X2 model and the
        // two goal-market models from it, serialize/deserialize the bundle, and
        // predict — this is the one integration point unit tests can't cover.
        csvDatasetService.generate();
        trainingService.train();
        smileOddsModel.loadModel();

        OddsResponseDto odds = smileOddsModel.predict(70, 60, 65, 55);

        float resultSum = odds.homeWinProbability() + odds.drawProbability() + odds.awayWinProbability();
        assertTrue(Math.abs(resultSum - 1.0f) < 0.01f, "1X2 probabilities should sum to ~1: " + resultSum);

        assertTrue(odds.over2_5Probability() >= 0f && odds.over2_5Probability() <= 1f);
        assertTrue(odds.over3_5Probability() >= 0f && odds.over3_5Probability() <= 1f);

        // Each line is binary, so over + under must sum to ~1.
        assertTrue(Math.abs(odds.over2_5Probability() + odds.under2_5Probability() - 1.0f) < 0.01f);
        assertTrue(Math.abs(odds.over3_5Probability() + odds.under3_5Probability() - 1.0f) < 0.01f);

        // Over 3.5 implies Over 2.5, so P(over 3.5) must never exceed P(over 2.5).
        assertTrue(odds.over3_5Probability() <= odds.over2_5Probability(),
                "over3_5Probability (%s) should be <= over2_5Probability (%s)"
                        .formatted(odds.over3_5Probability(), odds.over2_5Probability()));
    }
}

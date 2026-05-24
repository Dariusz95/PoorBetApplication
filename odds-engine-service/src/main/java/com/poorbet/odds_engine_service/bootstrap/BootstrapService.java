package com.poorbet.odds_engine_service.bootstrap;

import com.poorbet.odds_engine_service.dataset.CsvDatasetService;
import com.poorbet.odds_engine_service.lifecycle.SystemState;
import com.poorbet.odds_engine_service.lifecycle.SystemStatus;
import com.poorbet.odds_engine_service.ml.ModelStorageService;
import com.poorbet.odds_engine_service.ml.SmileOddsModel;
import com.poorbet.odds_engine_service.ml.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootstrapService {

    private final CsvDatasetService csvDatasetService;
    private final TrainingService trainingService;
    private final ModelStorageService modelStorageService;
    private final SystemState systemState;
    private final SmileOddsModel smileOddsModel;

    public void initIfNeeded() {
        systemState.set(SystemStatus.STARTING);

        try {
            log.info("Model exists {}", modelStorageService.modelExists());
            if (!modelStorageService.modelExists()) {

                systemState.set(SystemStatus.TRAINING);

                csvDatasetService.generateIfMissing();

                trainingService.train();
            }

            smileOddsModel.loadModel();

            systemState.set(SystemStatus.READY);

        } catch (Exception e) {

            systemState.setError();

            throw new IllegalStateException(
                    "Bootstrap failed",
                    e
            );
        }
    }
}

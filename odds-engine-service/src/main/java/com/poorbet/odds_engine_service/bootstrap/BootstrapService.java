package com.poorbet.odds_engine_service.bootstrap;

import com.poorbet.odds_engine_service.dataset.CsvDatasetService;
import com.poorbet.odds_engine_service.ml.ModelStorageService;
import com.poorbet.odds_engine_service.ml.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BootstrapService {

    private final CsvDatasetService csvDatasetService;
    private final TrainingService trainingService;
    private final ModelStorageService modelStorageService;
    private final SystemState systemState;

    public void initIfNeeded() {
        csvDatasetService.generateIfMissing();

        if (!modelStorageService.modelExists()) {
            trainingService.train();
        }

        systemState.markReady();
    }
}

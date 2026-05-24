package com.poorbet.odds_engine_service.dataset;

import com.poorbet.odds_engine_service.oddstraining.pipeline.TrainingPipeline;
import com.poorbet.odds_engine_service.oddstraining.properties.DatasetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvDatasetService {

    private final TrainingPipeline trainingPipeline;
    private final DatasetProperties properties;

    public boolean datasetExists() {
        return Files.exists(properties.getOutputPath());
    }

    public void generateIfMissing() {
        log.info("csv exists - {}", datasetExists());
        if (!datasetExists()) {
            generate();
        }
    }

    public void generate() {
        trainingPipeline.run();
    }
}
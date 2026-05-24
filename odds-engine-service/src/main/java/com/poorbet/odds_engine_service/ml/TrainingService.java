package com.poorbet.odds_engine_service.ml;

import com.poorbet.odds_engine_service.config.ModelProperties;
import com.poorbet.odds_engine_service.oddstraining.properties.DatasetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import smile.classification.LogisticRegression;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final ModelProperties modelProperties;
    private final DatasetProperties datasetProperties;

    public void train() {
        Path csvPath = datasetProperties.getOutputPath();

        try {

            List<CSVRecord> rows;

            try (
                    BufferedReader reader =
                            Files.newBufferedReader(csvPath);

                    CSVParser parser = CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .build()
                            .parse(reader)
            ) {

                rows = parser.getRecords();
            }

            double[][] x = new double[rows.size()][4];
            int[] y = new int[rows.size()];

            for (int i = 0; i < rows.size(); i++) {

                CSVRecord row = rows.get(i);

                x[i][0] =
                        Double.parseDouble(row.get("home_attack"));

                x[i][1] =
                        Double.parseDouble(row.get("home_defence"));

                x[i][2] =
                        Double.parseDouble(row.get("away_attack"));

                x[i][3] =
                        Double.parseDouble(row.get("away_defence"));

                y[i] = mapResult(row.get("result"));
            }

            LogisticRegression model =
                    LogisticRegression.fit(x, y);

            saveModel(model);

            log.info("Model trained successfully");

        } catch (Exception e) {
            log.error("Failed to train model", e);
            throw new RuntimeException(e);
        }
    }

    private void saveModel(LogisticRegression model) {

        Path modelPath = modelProperties.getPath();

        try {

            Files.createDirectories(modelPath.getParent());

            log.info("Saving model to {}", modelPath);

            try (ObjectOutputStream oos =
                         new ObjectOutputStream(
                                 Files.newOutputStream(modelPath)
                         )) {

                oos.writeObject(model);
            }

            log.info("Model saved successfully");

        } catch (Exception e) {

            log.error("Failed to save model", e);

            throw new RuntimeException(e);
        }
    }

    private int mapResult(String result) {

        return switch (result) {
            case "H" -> 0;
            case "X" -> 1;
            case "A" -> 2;
            default -> throw new IllegalArgumentException();
        };
    }
}
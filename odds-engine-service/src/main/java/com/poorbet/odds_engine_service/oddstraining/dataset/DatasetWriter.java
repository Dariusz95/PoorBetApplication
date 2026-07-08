package com.poorbet.odds_engine_service.oddstraining.dataset;


import com.poorbet.odds_engine_service.oddstraining.model.MatchRecord;
import com.poorbet.odds_engine_service.oddstraining.properties.DatasetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatasetWriter {

    private final DatasetProperties properties;

    public void write(List<MatchRecord> records) {
        Path path = properties.getOutputPath();
        String delimiter = properties.getDelimiter();

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            log.error("❌ Nie udało się utworzyć katalogu {}", path.getParent(), e);
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.join(delimiter,
                    "home_attack",
                    "home_defence",
                    "away_attack",
                    "away_defence",
                    "result"));
            writer.newLine();

            for (MatchRecord record : records) {
                writer.write(String.join(delimiter,
                        String.valueOf(record.getHomeAttack()),
                        String.valueOf(record.getHomeDefence()),
                        String.valueOf(record.getAwayAttack()),
                        String.valueOf(record.getAwayDefence()),
                        record.getResult()));
                writer.newLine();
            }

            log.info("✅ Zapisano {} rekordów do {}", records.size(), path);
        } catch (IOException e) {
            log.error("❌ Błąd zapisu CSV", e);
        }
    }
}

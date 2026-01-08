package com.poorbet.oddstraining.dataset;


import com.poorbet.oddstraining.model.MatchRecord;
import com.poorbet.oddstraining.properties.DatasetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatasetWriter {

    private final DatasetProperties properties;

    public void write(List<MatchRecord> records) {
        String path = properties.getOutputPath();
        String delimiter = properties.getDelimiter();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
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

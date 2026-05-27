package com.poorbet.odds_engine_service.oddstraining.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "dataset")
public class DatasetProperties {
    private Path outputPath;
    private String delimiter;
}
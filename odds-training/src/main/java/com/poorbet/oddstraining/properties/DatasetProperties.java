package com.poorbet.oddstraining.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "dataset")
public class DatasetProperties {
    private String outputPath;
    private String delimiter;
}
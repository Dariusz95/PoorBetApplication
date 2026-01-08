package com.poorbet.oddstraining.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "simulation.service")
public record SimulationServiceProperties(
        String url,
        Timeout timeout
) {
    public record Timeout(Duration connect, Duration read) {
    }
}

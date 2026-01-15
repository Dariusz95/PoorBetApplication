package com.poorbet.couponservice.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "match.service")
public record MatchProperties(
        @NotNull
        String url,
        @NotNull
        Timeout timeout
) {
    public record Timeout(Duration connect, Duration read) {
    }
}

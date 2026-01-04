package com.poorbet.matchservice.match.stream.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "match-pool")
@Validated
@Data
public class MatchPoolProperties {

    @Min(1)
    private int matchesPerPool;

    @Min(2)
    private int poolsInAdvance;

    @Min(1)
    private int poolIntervalMinutes;

    private long scheduleRateMs;
}

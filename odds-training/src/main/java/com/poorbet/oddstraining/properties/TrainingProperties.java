package com.poorbet.oddstraining.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "training")
public record TrainingProperties(
        @NotNull int countPerTier,
        @NotNull int repetitions
) {}

package com.poorbet.matchservice.match.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "teams.internal-api")
public record TeamsInternalAuthProperties(
        @NotNull
        String token
) {
}

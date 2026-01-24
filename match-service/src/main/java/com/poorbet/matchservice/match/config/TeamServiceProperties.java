package com.poorbet.matchservice.match.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "teams.service")
public record TeamServiceProperties (
        @NotNull
        String url,
        @NotNull
        Timeout timeout
){
    public record Timeout(Duration connect, Duration read){}
}

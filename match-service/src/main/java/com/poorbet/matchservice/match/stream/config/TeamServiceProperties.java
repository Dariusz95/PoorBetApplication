package com.poorbet.matchservice.match.stream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "teams.service")
public record TeamServiceProperties (
        String url,
        Timeout timeout
){
    public record Timeout(Duration connect, Duration read){}
}

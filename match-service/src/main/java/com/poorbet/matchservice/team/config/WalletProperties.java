package com.poorbet.matchservice.team.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "wallet.service")
public record WalletProperties(
        String url,
        Timeout timeout
) {
    public record Timeout(Duration connect, Duration read) {
    }
}

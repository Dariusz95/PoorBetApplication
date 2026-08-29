package com.poorbet.couponservice.infrastructure.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {

    private final Retry retry = new Retry();
    private final Cleanup cleanup = new Cleanup();

    @Getter
    @Setter
    public static class Retry {
        private int maxAttempts = 10;
        private Duration initialBackoff = Duration.ofMinutes(1);
        private Duration maxBackoff = Duration.ofHours(1);
    }

    @Getter
    @Setter
    public static class Cleanup {
        private Duration retention = Duration.ofDays(7);
    }
}

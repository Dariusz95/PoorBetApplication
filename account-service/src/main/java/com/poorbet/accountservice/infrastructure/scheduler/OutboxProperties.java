package com.poorbet.accountservice.infrastructure.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Tuning for the transactional outbox. Picked up automatically via {@code @ConfigurationPropertiesScan}
 * on the main application class. Override under the {@code outbox:} key in application.yml.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {

    private final Retry retry = new Retry();
    private final Cleanup cleanup = new Cleanup();

    @Getter
    @Setter
    public static class Retry {
        /** Total publish attempts allowed before an event is moved to DEAD_LETTER. */
        private int maxAttempts = 10;
        /** Delay before the first retry; subsequent retries grow exponentially. */
        private Duration initialBackoff = Duration.ofMinutes(1);
        /** Upper bound for the exponential backoff between retries. */
        private Duration maxBackoff = Duration.ofHours(1);
    }

    @Getter
    @Setter
    public static class Cleanup {
        /** How long a successfully published (SENT) event is kept before it is deleted. */
        private Duration retention = Duration.ofDays(7);
    }
}

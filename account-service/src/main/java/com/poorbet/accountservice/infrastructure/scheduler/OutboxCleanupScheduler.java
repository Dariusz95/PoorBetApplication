package com.poorbet.accountservice.infrastructure.scheduler;

import com.poorbet.accountservice.infrastructure.persistence.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Periodically removes events that have already been published, so the {@code outbox_event}
 * table does not grow without bound. Only SENT rows past the retention window are deleted;
 * NEW/FAILED rows are still in flight and DEAD_LETTER rows are kept for manual inspection.
 *
 * <p>No distributed lock: the delete is idempotent, so running it on several instances at once
 * is merely redundant, not harmful (the publish loop is unlocked for the same reason).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupScheduler {

    // Run once an hour; the publish loop is separate and runs every 5s.
    private static final long CLEANUP_INTERVAL_MS = 3_600_000L;

    private final OutboxRepository outboxRepository;
    private final OutboxProperties outboxProperties;

    @Scheduled(fixedDelay = CLEANUP_INTERVAL_MS, initialDelay = CLEANUP_INTERVAL_MS)
    @Transactional
    public void cleanupPublishedEvents() {
        Instant threshold = Instant.now().minus(outboxProperties.getCleanup().getRetention());
        int removed = outboxRepository.deleteSentBefore(threshold);
        if (removed > 0) {
            log.info("Outbox cleanup: removed {} SENT events older than {}", removed, threshold);
        }
    }
}

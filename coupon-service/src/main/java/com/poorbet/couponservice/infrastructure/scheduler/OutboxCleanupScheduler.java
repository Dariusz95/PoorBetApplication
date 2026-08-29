package com.poorbet.couponservice.infrastructure.scheduler;

import com.poorbet.couponservice.infrastructure.persistence.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupScheduler {

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

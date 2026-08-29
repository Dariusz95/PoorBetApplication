package com.poorbet.accountservice.infrastructure.persistence;


import com.poorbet.accountservice.infrastructure.persistence.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Picks up rows that still need publishing: freshly created ones (NEW) and previously
     * failed ones (FAILED) whose backoff window has elapsed. DEAD_LETTER and SENT are excluded.
     * FOR UPDATE SKIP LOCKED lets multiple instances process disjoint batches concurrently.
     */
    @Query(value = """
            SELECT * FROM outbox_event
            WHERE status IN ('NEW', 'FAILED')
              AND (next_retry_at IS NULL OR next_retry_at <= now())
            ORDER BY created_at
            LIMIT 100
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findPendingForUpdate();

    /**
     * Bulk-deletes events that were published successfully before the given threshold.
     * Returns the number of removed rows. Keeps the table from growing unbounded.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM outbox_event WHERE status = 'SENT' AND sent_at < :threshold",
            nativeQuery = true)
    int deleteSentBefore(@Param("threshold") Instant threshold);
}

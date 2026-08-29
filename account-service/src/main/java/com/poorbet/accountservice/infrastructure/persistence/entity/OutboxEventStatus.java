package com.poorbet.accountservice.infrastructure.persistence.entity;

public enum OutboxEventStatus {
    /** Just persisted, waiting for the first publish attempt. */
    NEW,
    /** Successfully published to RabbitMQ. Eligible for cleanup once old enough. */
    SENT,
    /** Last publish attempt failed; will be retried after next_retry_at. */
    FAILED,
    /** Retry budget exhausted. Requires manual intervention, never retried automatically. */
    DEAD_LETTER
}

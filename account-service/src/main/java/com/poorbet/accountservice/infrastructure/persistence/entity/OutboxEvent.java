package com.poorbet.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    private UUID id;

    private String exchange;

    private String routingKey;

    private String eventType;

    private String version;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    private Instant createdAt;

    private Instant sentAt;

    // Number of failed publish attempts so far (column added in V7, NOT NULL DEFAULT 0).
    @Column(nullable = false)
    private int retryCount;

    // Earliest time the next publish attempt may happen; null means "ready now".
    private Instant nextRetryAt;
}

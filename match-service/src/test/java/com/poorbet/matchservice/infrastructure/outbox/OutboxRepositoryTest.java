package com.poorbet.matchservice.infrastructure.outbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class OutboxRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("outbox_test")
            .withUsername("test")
            .withPassword("test");

    @MockitoBean
    private CacheManager cacheManager;

    @Autowired
    private OutboxRepository outboxRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.cache.type", () -> "none");
    }

    private OutboxEvent buildEvent(OutboxEventStatus status, Instant createdAt) {
        return OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange("match.exchange")
                .routingKey("match.finished")
                .eventType("MATCH_FINISHED")
                .version("v1")
                .payload("{}")
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll();
    }

    @Test
    void findPendingForUpdate_shouldReturnNewAndDueFailedEvents() {
        // Arrange
        OutboxEvent newEvent = buildEvent(OutboxEventStatus.NEW, Instant.now());
        OutboxEvent sentEvent = buildEvent(OutboxEventStatus.SENT, Instant.now());
        OutboxEvent failedDue = buildEvent(OutboxEventStatus.FAILED, Instant.now());
        OutboxEvent deadLetter = buildEvent(OutboxEventStatus.DEAD_LETTER, Instant.now());
        outboxRepository.saveAll(List.of(newEvent, sentEvent, failedDue, deadLetter));

        // Act
        List<OutboxEvent> pending = outboxRepository.findPendingForUpdate();

        // Assert
        assertThat(pending).extracting(OutboxEvent::getId)
                .containsExactlyInAnyOrder(newEvent.getId(), failedDue.getId());
    }

    @Test
    void findPendingForUpdate_shouldSkipFailedEventsStillInBackoff() {
        // Arrange
        OutboxEvent notDueYet = buildEvent(OutboxEventStatus.FAILED, Instant.now());
        notDueYet.setNextRetryAt(Instant.now().plus(10, ChronoUnit.MINUTES));

        OutboxEvent dueAgain = buildEvent(OutboxEventStatus.FAILED, Instant.now());
        dueAgain.setNextRetryAt(Instant.now().minus(1, ChronoUnit.MINUTES));

        outboxRepository.saveAll(List.of(notDueYet, dueAgain));

        // Act
        List<OutboxEvent> pending = outboxRepository.findPendingForUpdate();

        // Assert
        assertThat(pending).extracting(OutboxEvent::getId).containsExactly(dueAgain.getId());
    }

    @Test
    void findPendingForUpdate_shouldOrderByCreatedAtAscending() {
        // Arrange
        Instant now = Instant.now();
        OutboxEvent older = buildEvent(OutboxEventStatus.NEW, now.minus(1, ChronoUnit.HOURS));
        OutboxEvent newer = buildEvent(OutboxEventStatus.NEW, now);
        outboxRepository.saveAll(List.of(newer, older));

        // Act
        List<OutboxEvent> pending = outboxRepository.findPendingForUpdate();

        // Assert
        assertThat(pending).extracting(OutboxEvent::getId)
                .containsExactly(older.getId(), newer.getId());
    }

    @Test
    void findPendingForUpdate_shouldReturnEmpty_whenNoPendingEvents() {
        // Arrange
        outboxRepository.save(buildEvent(OutboxEventStatus.SENT, Instant.now()));

        // Act
        List<OutboxEvent> pending = outboxRepository.findPendingForUpdate();

        // Assert
        assertThat(pending).isEmpty();
    }

    @Test
    void deleteSentBefore_shouldRemoveOnlyOldSentEvents() {
        // Arrange
        Instant now = Instant.now();

        OutboxEvent oldSent = buildEvent(OutboxEventStatus.SENT, now.minus(30, ChronoUnit.DAYS));
        oldSent.setSentAt(now.minus(20, ChronoUnit.DAYS));

        OutboxEvent recentSent = buildEvent(OutboxEventStatus.SENT, now.minus(1, ChronoUnit.DAYS));
        recentSent.setSentAt(now.minus(1, ChronoUnit.DAYS));

        OutboxEvent oldButNew = buildEvent(OutboxEventStatus.NEW, now.minus(30, ChronoUnit.DAYS));
        OutboxEvent oldDeadLetter = buildEvent(OutboxEventStatus.DEAD_LETTER, now.minus(30, ChronoUnit.DAYS));

        outboxRepository.saveAll(List.of(oldSent, recentSent, oldButNew, oldDeadLetter));

        // Act
        int removed = outboxRepository.deleteSentBefore(now.minus(7, ChronoUnit.DAYS));

        // Assert
        assertThat(removed).isEqualTo(1);
        assertThat(outboxRepository.findAll()).extracting(OutboxEvent::getId)
                .containsExactlyInAnyOrder(recentSent.getId(), oldButNew.getId(), oldDeadLetter.getId());
    }
}

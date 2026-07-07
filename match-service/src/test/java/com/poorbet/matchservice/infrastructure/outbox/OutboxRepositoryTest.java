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
    void findPendingForUpdate_shouldReturnOnlyNewEvents() {
        // Arrange
        OutboxEvent newEvent = buildEvent(OutboxEventStatus.NEW, Instant.now());
        OutboxEvent sentEvent = buildEvent(OutboxEventStatus.SENT, Instant.now());
        OutboxEvent failedEvent = buildEvent(OutboxEventStatus.FAILED, Instant.now());
        outboxRepository.saveAll(List.of(newEvent, sentEvent, failedEvent));

        // Act
        List<OutboxEvent> pending = outboxRepository.findPendingForUpdate();

        // Assert
        assertThat(pending).extracting(OutboxEvent::getId).containsExactly(newEvent.getId());
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
    void findPendingForUpdate_shouldReturnEmpty_whenNoNewEvents() {
        // Arrange
        outboxRepository.save(buildEvent(OutboxEventStatus.SENT, Instant.now()));

        // Act
        List<OutboxEvent> pending = outboxRepository.findPendingForUpdate();

        // Assert
        assertThat(pending).isEmpty();
    }
}

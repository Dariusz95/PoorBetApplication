package com.poorbet.accountservice.infrastructure.scheduler;

import com.poorbet.accountservice.infrastructure.persistence.OutboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxCleanupScheduler Unit Tests")
class OutboxCleanupSchedulerTest {

    @Mock
    private OutboxRepository outboxRepository;
    @Spy
    private OutboxProperties outboxProperties = new OutboxProperties();

    @InjectMocks
    private OutboxCleanupScheduler scheduler;

    @Test
    @DisplayName("Should delete SENT events older than the configured retention window")
    void shouldDeleteEventsOlderThanRetention() {
        // Arrange
        outboxProperties.getCleanup().setRetention(Duration.ofDays(3));
        when(outboxRepository.deleteSentBefore(org.mockito.ArgumentMatchers.any())).thenReturn(5);

        Instant before = Instant.now();

        // Act
        scheduler.cleanupPublishedEvents();

        // Assert
        ArgumentCaptor<Instant> thresholdCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(outboxRepository).deleteSentBefore(thresholdCaptor.capture());

        Duration age = Duration.between(thresholdCaptor.getValue(), before);
        assertThat(age).isCloseTo(Duration.ofDays(3), Duration.ofSeconds(5));
    }
}

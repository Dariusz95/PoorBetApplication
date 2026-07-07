package com.poorbet.walletservice.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.walletservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.walletservice.infrastructure.persistence.entity.OutboxEvent;
import com.poorbet.walletservice.infrastructure.persistence.entity.OutboxEventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.poorbet.commons.rabbit.events.wallet.WalletEvents.WALLET_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxPublisher Unit Tests")
class OutboxPublisherTest {

    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private MessagingProperties messagingProperties;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    private OutboxEvent walletCreatedEvent;

    @BeforeEach
    void setUp() {
        walletCreatedEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange(WALLET_CREATED.exchange())
                .routingKey(WALLET_CREATED.routingKey())
                .eventType(WALLET_CREATED.eventType())
                .version(WALLET_CREATED.version())
                .payload("{\"userId\":\"" + UUID.randomUUID() + "\"}")
                .status(OutboxEventStatus.NEW)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should publish pending events to RabbitMQ and mark them as SENT")
    void shouldPublishPendingEventsSuccessfully() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(walletCreatedEvent));
        when(messagingProperties.getSourceService()).thenReturn("wallet-service");

        // Act
        outboxPublisher.publishEvents();

        // Assert
        ArgumentCaptor<EventEnvelope> envelopeCaptor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(rabbitTemplate).convertAndSend(eq(WALLET_CREATED.exchange()), eq(WALLET_CREATED.routingKey()), envelopeCaptor.capture());
        assertThat(envelopeCaptor.getValue().eventType()).isEqualTo(WALLET_CREATED.eventType());
        assertThat(envelopeCaptor.getValue().source()).isEqualTo("wallet-service");

        assertThat(walletCreatedEvent.getStatus()).isEqualTo(OutboxEventStatus.SENT);
        verify(outboxRepository).saveAll(List.of(walletCreatedEvent));
    }

    @Test
    @DisplayName("Should mark event as FAILED when publishing to RabbitMQ throws")
    void shouldMarkEventAsFailedWhenPublishingThrows() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(walletCreatedEvent));
        when(messagingProperties.getSourceService()).thenReturn("wallet-service");
        doThrowOnSend();

        // Act
        outboxPublisher.publishEvents();

        // Assert
        assertThat(walletCreatedEvent.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        verify(outboxRepository).saveAll(List.of(walletCreatedEvent));
    }

    private void doThrowOnSend() {
        org.mockito.Mockito.doThrow(new org.springframework.amqp.AmqpException("broker unavailable"))
                .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }

    @Test
    @DisplayName("Should not persist anything when an event has an unrecognized eventType")
    void shouldPropagateWhenEventTypeUnknown() {
        // Arrange
        OutboxEvent unknownEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange("wallet.exchange")
                .routingKey("wallet.unknown")
                .eventType("UNKNOWN_EVENT")
                .version("v1")
                .payload("{}")
                .status(OutboxEventStatus.NEW)
                .createdAt(Instant.now())
                .build();
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(unknownEvent));

        // Act & Assert
        assertThatThrownBy(() -> outboxPublisher.publishEvents())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unknown eventType");

        verify(outboxRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should do nothing when there are no pending events")
    void shouldDoNothingWhenNoPendingEvents() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of());

        // Act
        outboxPublisher.publishEvents();

        // Assert
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Object.class));
        verify(outboxRepository).saveAll(List.of());
    }
}

package com.poorbet.authservice.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.authservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.authservice.infrastructure.persistence.entity.OutboxEvent;
import com.poorbet.authservice.infrastructure.persistence.entity.OutboxEventStatus;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.MessagingProperties;
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

import static com.poorbet.commons.rabbit.events.auth.AuthEvents.USER_EVENTS;
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

    private OutboxEvent userCreatedEvent;

    @BeforeEach
    void setUp() {
        userCreatedEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange(USER_EVENTS.exchange())
                .routingKey(USER_EVENTS.routingKey())
                .eventType(USER_EVENTS.eventType())
                .version(USER_EVENTS.version())
                .payload("{\"userId\":\"" + UUID.randomUUID() + "\"}")
                .status(OutboxEventStatus.NEW)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should publish pending events to RabbitMQ and mark them as SENT")
    void shouldPublishPendingEventsSuccessfully() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(userCreatedEvent));
        when(messagingProperties.getSourceService()).thenReturn("auth-service");

        // Act
        outboxPublisher.publishEvents();

        // Assert
        ArgumentCaptor<EventEnvelope> envelopeCaptor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(rabbitTemplate).convertAndSend(eq(USER_EVENTS.exchange()), eq(USER_EVENTS.routingKey()), envelopeCaptor.capture());
        assertThat(envelopeCaptor.getValue().eventType()).isEqualTo(USER_EVENTS.eventType());
        assertThat(envelopeCaptor.getValue().source()).isEqualTo("auth-service");

        assertThat(userCreatedEvent.getStatus()).isEqualTo(OutboxEventStatus.SENT);
        verify(outboxRepository).saveAll(List.of(userCreatedEvent));
    }

    @Test
    @DisplayName("Should mark event as FAILED when publishing to RabbitMQ throws")
    void shouldMarkEventAsFailedWhenPublishingThrows() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(userCreatedEvent));
        when(messagingProperties.getSourceService()).thenReturn("auth-service");
        doThrowOnSend();

        // Act
        outboxPublisher.publishEvents();

        // Assert
        assertThat(userCreatedEvent.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        verify(outboxRepository).saveAll(List.of(userCreatedEvent));
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
                .exchange("auth.events")
                .routingKey("user.unknown")
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

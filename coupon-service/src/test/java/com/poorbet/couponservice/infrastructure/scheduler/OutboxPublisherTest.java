package com.poorbet.couponservice.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.couponservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.couponservice.infrastructure.persistence.entity.OutboxEvent;
import com.poorbet.couponservice.infrastructure.persistence.entity.OutboxEventStatus;
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.poorbet.commons.rabbit.events.coupon.CouponEvents.COUPON_LOST;
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
    @Spy
    private OutboxProperties outboxProperties = new OutboxProperties();

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    private OutboxEvent couponLostEvent;

    @BeforeEach
    void setUp() {
        couponLostEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange(COUPON_LOST.exchange())
                .routingKey(COUPON_LOST.routingKey())
                .eventType(COUPON_LOST.eventType())
                .version(COUPON_LOST.version())
                .payload("{\"id\":\"" + UUID.randomUUID() + "\"}")
                .status(OutboxEventStatus.NEW)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should publish pending events to RabbitMQ and mark them as SENT")
    void shouldPublishPendingEventsSuccessfully() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(couponLostEvent));
        when(messagingProperties.getSourceService()).thenReturn("coupon-service");

        // Act
        outboxPublisher.publishEvents();

        // Assert
        ArgumentCaptor<EventEnvelope> envelopeCaptor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(rabbitTemplate).convertAndSend(eq(COUPON_LOST.exchange()), eq(COUPON_LOST.routingKey()), envelopeCaptor.capture());
        assertThat(envelopeCaptor.getValue().eventType()).isEqualTo(COUPON_LOST.eventType());
        assertThat(envelopeCaptor.getValue().source()).isEqualTo("coupon-service");

        assertThat(couponLostEvent.getStatus()).isEqualTo(OutboxEventStatus.SENT);
        assertThat(couponLostEvent.getSentAt()).isNotNull();
        assertThat(couponLostEvent.getNextRetryAt()).isNull();
        verify(outboxRepository).saveAll(List.of(couponLostEvent));
    }

    @Test
    @DisplayName("Should mark event as FAILED and schedule a retry when publishing to RabbitMQ throws")
    void shouldMarkEventAsFailedWhenPublishingThrows() {
        // Arrange
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(couponLostEvent));
        when(messagingProperties.getSourceService()).thenReturn("coupon-service");
        doThrowOnSend();

        // Act
        outboxPublisher.publishEvents();

        // Assert
        assertThat(couponLostEvent.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(couponLostEvent.getRetryCount()).isEqualTo(1);
        assertThat(couponLostEvent.getNextRetryAt()).isAfter(Instant.now());
        verify(outboxRepository).saveAll(List.of(couponLostEvent));
    }

    @Test
    @DisplayName("Should grow the retry delay exponentially with the attempt count")
    void shouldBackOffExponentially() {
        // Arrange: this is the 3rd attempt -> initialBackoff (1m) * 2^2 = 4 minutes
        couponLostEvent.setRetryCount(2);
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(couponLostEvent));
        when(messagingProperties.getSourceService()).thenReturn("coupon-service");
        doThrowOnSend();

        Instant before = Instant.now();

        // Act
        outboxPublisher.publishEvents();

        // Assert
        assertThat(couponLostEvent.getRetryCount()).isEqualTo(3);
        Duration delay = Duration.between(before, couponLostEvent.getNextRetryAt());
        assertThat(delay).isBetween(Duration.ofMinutes(3), Duration.ofMinutes(5));
    }

    @Test
    @DisplayName("Should move event to DEAD_LETTER once the retry budget is exhausted")
    void shouldMoveToDeadLetterWhenRetryBudgetExhausted() {
        // Arrange: default maxAttempts is 10, so the 10th attempt is terminal
        couponLostEvent.setRetryCount(9);
        when(outboxRepository.findPendingForUpdate()).thenReturn(List.of(couponLostEvent));
        when(messagingProperties.getSourceService()).thenReturn("coupon-service");
        doThrowOnSend();

        // Act
        outboxPublisher.publishEvents();

        // Assert
        assertThat(couponLostEvent.getStatus()).isEqualTo(OutboxEventStatus.DEAD_LETTER);
        assertThat(couponLostEvent.getRetryCount()).isEqualTo(10);
        assertThat(couponLostEvent.getNextRetryAt()).isNull();
        verify(outboxRepository).saveAll(List.of(couponLostEvent));
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
                .exchange("coupon.exchange")
                .routingKey("coupon.unknown")
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

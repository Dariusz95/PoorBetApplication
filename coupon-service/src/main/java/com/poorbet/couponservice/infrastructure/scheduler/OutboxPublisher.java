package com.poorbet.couponservice.infrastructure.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.commons.rabbit.events.coupon.CouponCreationFailedEvent;
import com.poorbet.commons.rabbit.events.coupon.CouponLostEvent;
import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import com.poorbet.couponservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.couponservice.infrastructure.persistence.entity.OutboxEvent;
import com.poorbet.couponservice.infrastructure.persistence.entity.OutboxEventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.poorbet.commons.rabbit.events.coupon.CouponEvents.COUPON_CREATION_FAILED;
import static com.poorbet.commons.rabbit.events.coupon.CouponEvents.COUPON_LOST;
import static com.poorbet.commons.rabbit.events.coupon.CouponEvents.COUPON_WON;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;
    private final ObjectMapper objectMapper;
    private final OutboxProperties outboxProperties;
    private final Map<String, Class<?>> eventTypeMap = Map.of(
            COUPON_WON.eventType(), CouponWonEvent.class,
            COUPON_LOST.eventType(), CouponLostEvent.class,
            COUPON_CREATION_FAILED.eventType(), CouponCreationFailedEvent.class
    );

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findPendingForUpdate();

        for (OutboxEvent event : events) {
            Object payloadObject = toObject(event.getPayload(), event.getEventType());

            EventEnvelope<Object> envelope = new EventEnvelope<>(
                    event.getId(),
                    event.getEventType(),
                    event.getVersion(),
                    messagingProperties.getSourceService(),
                    payloadObject
            );

            try {
                rabbitTemplate.convertAndSend(
                        event.getExchange(),
                        event.getRoutingKey(),
                        envelope
                );
                markSent(event);
            } catch (Exception e) {
                markForRetry(event, e);
            }
        }

        outboxRepository.saveAll(events);
    }

    private void markSent(OutboxEvent event) {
        event.setStatus(OutboxEventStatus.SENT);
        event.setSentAt(Instant.now());
        event.setNextRetryAt(null);
    }

    private void markForRetry(OutboxEvent event, Exception cause) {
        int attempts = event.getRetryCount() + 1;
        event.setRetryCount(attempts);

        if (attempts >= outboxProperties.getRetry().getMaxAttempts()) {
            event.setStatus(OutboxEventStatus.DEAD_LETTER);
            event.setNextRetryAt(null);
            log.error("Outbox event {} moved to DEAD_LETTER after {} failed attempts",
                    event.getId(), attempts, cause);
        } else {
            Instant nextRetryAt = Instant.now().plus(backoffFor(attempts));
            event.setStatus(OutboxEventStatus.FAILED);
            event.setNextRetryAt(nextRetryAt);
            log.warn("Failed to publish outbox event {} (attempt {}), next retry at {}",
                    event.getId(), attempts, nextRetryAt, cause);
        }
    }

    private Duration backoffFor(int attempts) {
        OutboxProperties.Retry retry = outboxProperties.getRetry();
        long multiplier = 1L << Math.min(attempts - 1, 32);
        Duration backoff = retry.getInitialBackoff().multipliedBy(multiplier);
        return backoff.compareTo(retry.getMaxBackoff()) > 0 ? retry.getMaxBackoff() : backoff;
    }

    private Object toObject(String payload, String eventType) {
        try {
            Class<?> clazz = eventTypeMap.get(eventType);

            if (clazz == null) {
                throw new RuntimeException("Unknown eventType: " + eventType);
            }

            return objectMapper.readValue(payload, clazz);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize payload", e);
        }
    }
}

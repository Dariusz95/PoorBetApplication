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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

                event.setStatus("SENT");

            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                event.setStatus("FAILED");
            }
        }

        outboxRepository.saveAll(events);
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

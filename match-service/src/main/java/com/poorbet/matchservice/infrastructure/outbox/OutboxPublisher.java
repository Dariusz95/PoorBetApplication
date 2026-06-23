package com.poorbet.matchservice.infrastructure.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.commons.rabbit.events.match.MatchEvents;
import com.poorbet.commons.rabbit.events.match.MatchesFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;
    private final ObjectMapper objectMapper;

    private final Map<String, Class<?>> eventTypeMap = Map.of(
            MatchEvents.MATCH_FINISHED.eventType(), MatchesFinishedEvent.class
    );

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findPendingForUpdate();

        for (OutboxEvent event : events) {
            Object payloadObject = deserialize(event.getPayload(), event.getEventType());

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
                event.setStatus(OutboxEventStatus.SENT);
            } catch (Exception e) {
                log.error("Nie udało się opublikować eventu outbox {}", event.getId(), e);
                event.setStatus(OutboxEventStatus.FAILED);
            }
        }

        outboxRepository.saveAll(events);
    }

    private Object deserialize(String payload, String eventType) {
        try {
            Class<?> clazz = eventTypeMap.get(eventType);
            if (clazz == null) {
                throw new RuntimeException("Nieznany eventType: " + eventType);
            }
            return objectMapper.readValue(payload, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Nie udało się zdeserializować payloadu", e);
        }
    }
}

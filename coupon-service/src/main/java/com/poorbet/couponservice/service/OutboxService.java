package com.poorbet.couponservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.couponservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.couponservice.infrastructure.persistence.entity.OutboxEvent;
import com.poorbet.couponservice.infrastructure.persistence.entity.OutboxEventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void saveEvent(EventDefinition<?> definition, Object payload) {
        saveEvent(
                definition.exchange(),
                definition.routingKey(),
                definition.eventType(),
                definition.version(),
                payload
        );
    }

    public void saveEvent(String exchange, String routingKey, String eventType, String version, Object payload) {

        OutboxEvent event = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange(exchange)
                .routingKey(routingKey)
                .eventType(eventType)
                .version(version)
                .payload(toJson(payload))
                .status(OutboxEventStatus.NEW)
                .createdAt(Instant.now())
                .build();

        outboxRepository.save(event);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize event", e);
        }
    }
}

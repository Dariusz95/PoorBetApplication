package com.poorbet.walletservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.walletservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.walletservice.infrastructure.persistence.entity.OutboxEvent;
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

        OutboxEvent event = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .exchange(definition.exchange())
                .routingKey(definition.routingKey())
                .eventType(definition.eventType())
                .version(definition.version())
                .payload(toJson(payload))
                .status("NEW")
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
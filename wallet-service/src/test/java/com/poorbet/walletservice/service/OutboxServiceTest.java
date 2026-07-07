package com.poorbet.walletservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import com.poorbet.walletservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.walletservice.infrastructure.persistence.entity.OutboxEvent;
import com.poorbet.walletservice.infrastructure.persistence.entity.OutboxEventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxService Unit Tests")
class OutboxServiceTest {

    private final OutboxRepository outboxRepository = org.mockito.Mockito.mock(OutboxRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final OutboxService outboxService = new OutboxService(outboxRepository, objectMapper);

    @Test
    @DisplayName("Should persist a NEW outbox event with serialized payload from an EventDefinition")
    void shouldSaveEventFromDefinition() {
        // Arrange
        UUID userId = UUID.randomUUID();
        WalletCreatedEvent payload = new WalletCreatedEvent(userId);
        EventDefinition<WalletCreatedEvent> definition = new EventDefinition<>("wallet.exchange", "WALLET_CREATED", "v1");

        // Act
        outboxService.saveEvent(definition, payload);

        // Assert
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getExchange()).isEqualTo(definition.exchange());
        assertThat(saved.getRoutingKey()).isEqualTo(definition.routingKey());
        assertThat(saved.getEventType()).isEqualTo(definition.eventType());
        assertThat(saved.getVersion()).isEqualTo(definition.version());
        assertThat(saved.getStatus()).isEqualTo(OutboxEventStatus.NEW);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getPayload()).contains(userId.toString());
    }

    @Test
    @DisplayName("Should wrap serialization failures in a RuntimeException")
    void shouldWrapSerializationFailure() {
        // Arrange: an object the ObjectMapper cannot serialize (self-referencing, no getters)
        Object unserializable = new Object() {
            @SuppressWarnings("unused")
            public Object getSelf() {
                return this;
            }
        };
        EventDefinition<Object> definition = new EventDefinition<>("ex", "TYPE", "v1");

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> outboxService.saveEvent(definition, unserializable));
    }
}

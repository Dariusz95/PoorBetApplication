package com.poorbet.walletservice.infrastructure.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.wallet.WalletBalanceChangedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import com.poorbet.walletservice.config.MessagingProperties;
import com.poorbet.walletservice.infrastructure.persistence.OutboxRepository;
import com.poorbet.walletservice.infrastructure.persistence.entity.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.poorbet.commons.rabbit.events.wallet.WalletEvents.WALLET_BALANCE_CHANGED;
import static com.poorbet.commons.rabbit.events.wallet.WalletEvents.WALLET_CREATED;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;
    private final ObjectMapper objectMapper;
    private final Map<String, Class<?>> eventTypeMap = Map.of(
            WALLET_CREATED.eventType(), WalletCreatedEvent.class,
            WALLET_BALANCE_CHANGED.eventType(), WalletBalanceChangedEvent.class
    );

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findTop100ByStatus("NEW");


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
                event.setStatus("FAILED");
            }

            outboxRepository.save(event);
        }
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

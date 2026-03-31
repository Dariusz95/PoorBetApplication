package com.poorbet.matchservice.match.config.rabbitmq;

import com.poorbet.commons.rabbit.EventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitDomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(
            String exchange,
            String routingKey,
            String eventType,
            String eventVersion,
            String sourceService,
            Object payload) {
        EventEnvelope<Object> envelope = new EventEnvelope<>(
                eventType,
                eventVersion,
                Instant.now(),
                sourceService,
                UUID.randomUUID().toString(),
                payload
        );

        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                envelope
        );
    }
}

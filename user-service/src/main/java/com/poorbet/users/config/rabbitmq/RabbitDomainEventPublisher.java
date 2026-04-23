package com.poorbet.users.config.rabbitmq;

import com.poorbet.commons.rabbit.EventDefinition;
import com.poorbet.commons.rabbit.EventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitDomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public <T> void publish(
            EventDefinition<T> event, T payload, String source
    ) {
        EventEnvelope<T> envelope = new EventEnvelope<>(
                event.eventType(),
                event.version(),
                source,
                payload
        );

        rabbitTemplate.convertAndSend(
                event.exchange(),
                event.routingKey(),
                envelope
        );
    }
}

package com.poorbet.users.user.service;

import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.commons.rabbit.events.user.UserCreatedEvent;
import com.poorbet.commons.rabbit.events.user.UserEvents;
import com.poorbet.users.config.rabbitmq.RabbitDomainEventPublisher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserCreatedEventPublisher {

    private final RabbitDomainEventPublisher publisher;
    private final MessagingProperties properties;

    public void publishUserCreated(UUID userId) {
        publisher.publish(
                UserEvents.USER_EVENTS,
                new UserCreatedEvent(userId),
                properties.getSourceService()

        );
    }
}

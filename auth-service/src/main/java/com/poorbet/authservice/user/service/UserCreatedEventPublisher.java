package com.poorbet.authservice.user.service;

import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.commons.rabbit.events.auth.UserCreatedEvent;
import com.poorbet.commons.rabbit.events.auth.AuthEvents;
import com.poorbet.authservice.config.rabbitmq.RabbitDomainEventPublisher;
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
                AuthEvents.USER_EVENTS,
                new UserCreatedEvent(userId),
                properties.getSourceService()

        );
    }
}

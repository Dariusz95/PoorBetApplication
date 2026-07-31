package com.poorbet.authservice.user.service;

import com.poorbet.commons.rabbit.events.auth.UserCreatedEvent;
import com.poorbet.commons.rabbit.events.auth.AuthEvents;
import com.poorbet.authservice.service.OutboxService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserCreatedEventPublisher {

    private final OutboxService outboxService;

    public void publishUserCreated(UUID userId) {
        outboxService.saveEvent(AuthEvents.USER_EVENTS, new UserCreatedEvent(userId));
    }
}

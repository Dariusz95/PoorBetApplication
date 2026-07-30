package com.poorbet.notificationservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.account.AccountEvents;
import com.poorbet.commons.rabbit.events.account.AccountProgressChangedEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@RequiredArgsConstructor
public class AccountProgressListener {

    private final SseNotificationService sseNotificationService;

    @RabbitListener(queues = "${messaging.consumers.ACCOUNT_PROGRESS_CHANGED.queue}")
    public void onAccountProgressChanged(@Valid EventEnvelope<AccountProgressChangedEvent> eventEnvelope) {
        sseNotificationService.publish(
                eventEnvelope.payload().userId(),
                AccountEvents.ACCOUNT_PROGRESS_CHANGED.eventType(),
                eventEnvelope.payload()
        );
    }
}

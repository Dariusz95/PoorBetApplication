package com.poorbet.notificationservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.wallet.WalletEvents;
import com.poorbet.commons.rabbit.events.wallet.WalletBalanceChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletBalanceListener {

    private final SseNotificationService sseNotificationService;

    @RabbitListener(queues = "${messaging.consumers.wallet-balance-changed.queue}")
    public void onWalletUpdated(EventEnvelope<WalletBalanceChangedEvent> eventEnvelope) {
        sseNotificationService.publish(
                eventEnvelope.payload().userId(),
                WalletEvents.WALLET_BALANCE_CHANGED.eventType(),
                eventEnvelope.payload()
        );
    }
}

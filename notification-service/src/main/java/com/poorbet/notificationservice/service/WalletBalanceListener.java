package com.poorbet.notificationservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.wallet.WalletBalanceChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletBalanceListener {

    private final WalletNotificationService walletNotificationService;

    @RabbitListener(queues = "${messaging.wallet-queue}")
    public void onWalletUpdated(EventEnvelope<WalletBalanceChangedEvent> eventEnvelope) {
        walletNotificationService.publish(eventEnvelope.payload());
    }
}

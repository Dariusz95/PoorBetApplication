package com.poorbet.notificationservice.service;

import com.poorbet.notificationservice.dto.WalletBalanceChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletBalanceListener {

    private final WalletNotificationService walletNotificationService;

    @RabbitListener(queues = "${messaging.wallet-queue}")
    public void onWalletUpdated(WalletBalanceChangedEvent event) {
        walletNotificationService.publish(event);
    }
}

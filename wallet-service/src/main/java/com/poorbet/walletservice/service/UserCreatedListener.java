package com.poorbet.walletservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.walletservice.event.WalletBalanceChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreatedListener {

    @RabbitListener(queues = "${messaging.consumers.user-created.queue}")
    public void handleWalletChange(EventEnvelope<WalletBalanceChangedEvent> event) {
        log.info("📨 [WALLET] Received eventType={} version={} source={}",
                event.eventType(),
                event.version(),
                event.source());

    }
}

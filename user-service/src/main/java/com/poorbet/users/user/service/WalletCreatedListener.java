package com.poorbet.users.user.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletCreatedListener {

    @RabbitListener(queues = "${messaging.consumers.wallet-created.queue}")
    public void handleWalletCreated(EventEnvelope<WalletCreatedEvent> event) {
        log.info("📨 [USER] Received eventType={} version={} source={}",
                event.eventType(),
                event.version(),
                event.source());

    }
}

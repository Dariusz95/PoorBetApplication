package com.poorbet.authservice.user.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class WalletCreatedListener {

    @RabbitListener(queues = "${messaging.consumers.WALLET_CREATED.queue}")
    public void handleWalletCreated(@Valid EventEnvelope<WalletCreatedEvent> event) {
        log.info("📨 [USER] Received eventType={} version={} source={}",
                event.eventType(),
                event.version(),
                event.source());

    }
}

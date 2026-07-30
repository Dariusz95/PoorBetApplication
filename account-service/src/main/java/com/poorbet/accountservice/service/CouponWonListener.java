package com.poorbet.accountservice.service;

import com.poorbet.accountservice.domain.model.ProcessedEvent;
import com.poorbet.accountservice.repository.ProcessedEventRepository;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class CouponWonListener {

    private final WalletService walletService;
    private final ProgressService progressService;
    private final ProcessedEventRepository processedEventRepository;

    @RabbitListener(queues = "${messaging.consumers.COUPON_WON.queue}")
    @Transactional
    public void handleCouponWon(@Valid EventEnvelope<CouponWonEvent> event) {
        log.info("📨 [WALLET] Received eventType={} version={} source={}",
                event.eventType(),
                event.version(),
                event.source());

        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Event {} already processed, skipping", event.eventId());
            return;
        }

        progressService.addExpForStake(event.payload().userId(), event.payload().stake());
        walletService.handleCouponWon(event.payload());

        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }
}

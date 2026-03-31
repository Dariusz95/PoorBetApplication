package com.poorbet.couponservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.couponservice.dto.MatchesFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchesFinishedListener {

    private final CouponProcessingService couponProcessingService;

    @RabbitListener(queues = "${messaging.consumers.match-finished.queue}")
    public void handleFinishedMatches(EventEnvelope<MatchesFinishedEvent> envelope) {
        log.info("📨 [COUPON] Received eventType={} version={} source={}",
                envelope.eventType(),
                envelope.eventVersion(),
                envelope.sourceService());

        couponProcessingService.processFinishedMatch(envelope.payload());
    }
}

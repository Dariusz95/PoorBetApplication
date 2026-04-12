package com.poorbet.couponservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.match.MatchEvents;
import com.poorbet.commons.rabbit.events.match.MatchesFinishedEvent;
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
    public void handleFinishedMatches(EventEnvelope<MatchesFinishedEvent> event) {

        if (!event.eventType().equals(MatchEvents.MATCH_FINISHED.eventType())) {
            return;
        }

        log.info("📨 [COUPON] Received eventType={} version={} source={}",
                event.eventType(),
                event.version(),
                event.source());

        couponProcessingService.processFinishedMatch(event.payload());
    }
}

package com.poorbet.couponservice.service;

import com.poorbet.couponservice.config.RabbitConfig;
import com.poorbet.couponservice.dto.MatchesFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchesFinishedListener {

    private final CouponProcessingService couponProcessingService;

    @RabbitListener(queues = RabbitConfig.MATCH_FINISHED_QUEUE)
    public void handleFinishedMatches(MatchesFinishedEvent event) {
        List<UUID> matchIds = event.matchIds();
        log.info("🔥 [COUPON] Processing finished matches: {}", matchIds);
        couponProcessingService.processFinishedMatch(matchIds);
    }
}


package com.poorbet.couponservice.service;

import com.poorbet.couponservice.config.RabbitConfig;
import com.poorbet.couponservice.dto.MatchPoolFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PoolFinishedListener {


    @RabbitListener(queues = RabbitConfig.QUEUE_COUPON_POOL_FINISHED)
    public void handlePoolFinished(MatchPoolFinishedEvent event) {

        UUID poolId = event.matchPoolId();

        log.info("🔥 [COUPON] Received POOL FINISHED for pool {}", poolId);
    }
}


package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.matchservice.match.config.rabbitmq.RabbitConfig;
import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.dto.MatchesFinishedEvent;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchPoolEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishMatchesFinished(List<MatchResultDto> results) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.MATCH_EVENTS_EXCHANGE,
                "",
                new MatchesFinishedEvent(results)
        );
    }
}

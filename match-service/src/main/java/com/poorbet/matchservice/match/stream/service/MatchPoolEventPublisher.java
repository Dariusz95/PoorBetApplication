package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.MatchesFinishedEvent;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MatchPoolEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishMatchesFinished(List<UUID> matchIds) {
        rabbitTemplate.convertAndSend(
                "match.events",
                "",
                new MatchesFinishedEvent(matchIds)
        );
    }
}

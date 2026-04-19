package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.commons.rabbit.MessagingProperties;
import com.poorbet.commons.rabbit.events.match.MatchEvents;
import com.poorbet.commons.rabbit.events.match.MatchesFinishedEvent;
import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import com.poorbet.matchservice.match.config.rabbitmq.RabbitDomainEventPublisher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchPoolEventPublisher {

    private final RabbitDomainEventPublisher publisher;
    private final MessagingProperties properties;

    public void publishMatchesFinished(List<MatchResultEventDto> results) {
        publisher.publish(
                MatchEvents.MATCH_FINISHED,
                new MatchesFinishedEvent(results),
                properties.getSourceService()

        );
    }
}

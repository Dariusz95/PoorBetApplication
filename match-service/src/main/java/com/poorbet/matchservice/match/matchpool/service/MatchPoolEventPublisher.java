package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.commons.rabbit.events.match.MatchEvents;
import com.poorbet.commons.rabbit.events.match.MatchesFinishedEvent;
import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import com.poorbet.matchservice.match.config.rabbitmq.MessagingProperties;
import com.poorbet.matchservice.match.config.rabbitmq.RabbitDomainEventPublisher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchPoolEventPublisher {

    private final RabbitDomainEventPublisher rabbitDomainEventPublisher;
    private final MessagingProperties messagingProperties;

    public void publishMatchesFinished(List<MatchResultEventDto> results) {
        rabbitDomainEventPublisher.publish(
                messagingProperties.getExchanges().get("match"),
                MatchEvents.MATCH_FINISHED.routingKey(),
                MatchEvents.MATCH_FINISHED.eventType(),
                MatchEvents.MATCH_FINISHED.version(),
                messagingProperties.getSourceService(),
                new MatchesFinishedEvent(results)
        );
    }
}

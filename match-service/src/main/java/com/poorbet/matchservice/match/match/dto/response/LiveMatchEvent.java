package com.poorbet.matchservice.match.match.dto.response;

import com.poorbet.matchservice.match.matchpool.domain.MatchEventType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LiveMatchEvent {
    UUID matchId;
    int minute;
    int homeGoals;
    int awayGoals;
    MatchEventType eventType;
    String eventData;
}

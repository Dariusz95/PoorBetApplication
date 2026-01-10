package com.poorbet.matchservice.match.stream.dto.response;

import com.poorbet.matchservice.match.stream.model.enums.MatchEventType;
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

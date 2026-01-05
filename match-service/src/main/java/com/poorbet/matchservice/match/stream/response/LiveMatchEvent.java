package com.poorbet.matchservice.match.stream.response;

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
    boolean finished;
}

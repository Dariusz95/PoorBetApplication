package com.poorbet.matchservice.match.stream.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LiveMatchEvent {
    private UUID id;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private int homeScore;
    private int awayScore;
    private int minute;

    public static LiveMatchEvent heartbeat() {
        return new LiveMatchEvent(null, null, null, 0, 0, 0);
    }
}

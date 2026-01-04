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
    private String homeTeamName;
    private String awayTeamName;
    private int homeScore;
    private int awayScore;
    private int minute;
    private boolean isFinished;

    public static LiveMatchEvent heartbeat() {
        return new LiveMatchEvent(null, null, null,null,null, 0, 0, 0, false);
    }

    public boolean isFinished() {
        return isFinished;
    }
}

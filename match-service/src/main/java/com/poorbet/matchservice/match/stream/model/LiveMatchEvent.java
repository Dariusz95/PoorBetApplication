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
    private boolean isFinished;

    public static LiveMatchEvent heartbeat() {
        return new LiveMatchEvent(null, null, null, 0, 0, 0, false);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public LiveMatchEvent withUpdatedState(int minute, int homeScore, int awayScore, boolean finished) {
        return new LiveMatchEvent(this.id, this.homeTeamId, this.awayTeamId, minute, homeScore, awayScore, finished);
    }
}

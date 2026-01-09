package com.poorbet.matchservice.match.stream.dto;

import com.poorbet.matchservice.match.stream.dto.response.LiveMatchEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LiveMatchEventDto {
    private UUID id;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private int homeScore;
    private int awayScore;
    private int minute;
    private boolean isFinished;

    public static LiveMatchEventDto heartbeat() {
        return new LiveMatchEventDto(null, null, null, 0, 0, 0, false);
    }

    public static LiveMatchEventDto fromEvent(
            LiveMatchEvent event,
            TeamStatsDto home,
            TeamStatsDto away
    ) {
        return new LiveMatchEventDto(
                event.getMatchId(),
                home.getId(),
                away.getId(),
                event.getHomeGoals(),
                event.getAwayGoals(),
                event.getMinute(),
                event.isFinished()
        );
    }

    public boolean isFinished() {
        return isFinished;
    }
}

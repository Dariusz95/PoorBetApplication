package com.poorbet.matchservice.match.stream.model;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.response.LiveMatchEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LiveMatchEventDto {
    private UUID id;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private String homeTeamName;
    private String awayTeamName;
    private int homeScore;
    private int awayScore;
    private int minute;
    private boolean isFinished;

    public static LiveMatchEventDto heartbeat() {
        return new LiveMatchEventDto(null, null, null,null,null, 0, 0, 0, false);
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
                home.getName(),
                away.getName(),
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

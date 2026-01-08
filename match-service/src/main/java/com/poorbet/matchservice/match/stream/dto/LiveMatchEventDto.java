package com.poorbet.matchservice.match.stream.dto;

import com.poorbet.matchservice.match.stream.dto.response.LiveMatchEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LiveMatchEventDto {
    private UUID id;
    private TeamScoreDto homeTeam;
    private TeamScoreDto awayTeam;
    private int minute;
    private boolean isFinished;

    public static LiveMatchEventDto heartbeat() {
        return new LiveMatchEventDto(null, null, null, 0, false);
    }

    public static LiveMatchEventDto fromEvent(
            LiveMatchEvent event,
            TeamStatsDto home,
            TeamStatsDto away
    ) {
        return new LiveMatchEventDto(
                event.getMatchId(),
                new TeamScoreDto(home.getId(), home.getName(), event.getHomeGoals()),
                new TeamScoreDto(away.getId(), away.getName(), event.getAwayGoals()),
                event.getMinute(),
                event.isFinished()
        );
    }

    public boolean isFinished() {
        return isFinished;
    }
}

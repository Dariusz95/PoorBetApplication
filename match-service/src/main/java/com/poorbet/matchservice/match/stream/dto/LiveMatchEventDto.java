package com.poorbet.matchservice.match.stream.dto;

import com.poorbet.matchservice.match.stream.dto.response.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.model.enums.MatchEventType;
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
    private MatchEventType eventType;

    private String eventData;

    public static LiveMatchEventDto heartbeat() {
        return new LiveMatchEventDto(null, null, null, 0, 0, 0, MatchEventType.HEARTBEAT, null);
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
                event.getEventType(),
                event.getEventData()
        );
    }

    public boolean isFinished() {
        return eventType == MatchEventType.MATCH_ENDED;
    }
}

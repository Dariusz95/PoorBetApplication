package com.poorbet.matchservice.match.stream.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchUpdate {
    private String matchId;
    private int minute;
    private int homeScore;
    private int awayScore;
}
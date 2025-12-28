package com.poorbet.matchservice.match.stream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveMatchEvent {
    private int minute;
    private int homeGoals;
    private int awayGoals;
}

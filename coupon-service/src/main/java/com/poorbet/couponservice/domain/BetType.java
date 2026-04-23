package com.poorbet.couponservice.domain;

import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;

public enum BetType {
    HOME_WIN,
    DRAW,
    AWAY_WIN;


    public BetStatus mapToStatus(MatchResultEventDto result, int homeGoals, int awayGoals) {
        if (result == null) return BetStatus.PENDING;

        return switch (this) {
            case HOME_WIN -> homeGoals > awayGoals ? BetStatus.WON : BetStatus.LOST;
            case AWAY_WIN -> awayGoals > homeGoals ? BetStatus.WON : BetStatus.LOST;
            case DRAW -> homeGoals == awayGoals ? BetStatus.WON : BetStatus.LOST;
        };
    }
}

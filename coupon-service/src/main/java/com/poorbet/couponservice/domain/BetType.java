package com.poorbet.couponservice.domain;

import com.poorbet.couponservice.dto.MatchResultDto;

public enum BetType {
    HOME_WIN,
    DRAW,
    AWAY_WIN;


    public BetStatus mapToStatus(MatchResultDto result, int homeGoals, int awayGoals) {
        if (result == null) return BetStatus.PENDING;

        return switch (this) {
            case HOME_WIN -> homeGoals > awayGoals ? BetStatus.WON : BetStatus.LOST;
            case AWAY_WIN -> awayGoals > homeGoals ? BetStatus.WON : BetStatus.LOST;
            case DRAW -> homeGoals == awayGoals ? BetStatus.WON : BetStatus.LOST;
        };
    }
}

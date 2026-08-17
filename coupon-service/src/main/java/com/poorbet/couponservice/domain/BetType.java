package com.poorbet.couponservice.domain;

import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;

public enum BetType {
    HOME_WIN,
    DRAW,
    AWAY_WIN,
    OVER_2_5,
    UNDER_2_5,
    OVER_3_5,
    UNDER_3_5;


    public BetStatus mapToStatus(MatchResultEventDto result, int homeGoals, int awayGoals) {
        if (result == null) return BetStatus.PENDING;

        int totalGoals = homeGoals + awayGoals;

        return switch (this) {
            case HOME_WIN -> homeGoals > awayGoals ? BetStatus.WON : BetStatus.LOST;
            case AWAY_WIN -> awayGoals > homeGoals ? BetStatus.WON : BetStatus.LOST;
            case DRAW -> homeGoals == awayGoals ? BetStatus.WON : BetStatus.LOST;
            case OVER_2_5 -> totalGoals > 2 ? BetStatus.WON : BetStatus.LOST;
            case UNDER_2_5 -> totalGoals <= 2 ? BetStatus.WON : BetStatus.LOST;
            case OVER_3_5 -> totalGoals > 3 ? BetStatus.WON : BetStatus.LOST;
            case UNDER_3_5 -> totalGoals <= 3 ? BetStatus.WON : BetStatus.LOST;
        };
    }
}

package com.poorbet.couponservice.domain;

import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bet.settle() Unit Tests")
class BetSettleTest {

    private Bet buildBet(BetType betType) {
        return Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .homeTeamName("Home FC")
                .awayTeamName("Away FC")
                .matchStartTime(OffsetDateTime.now())
                .betType(betType)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(2.0))
                .build();
    }

    @Test
    @DisplayName("Should store home and away goals after settling")
    void should_setHomeAndAwayGoals_when_betSettled() {
        Bet bet = buildBet(BetType.HOME_WIN);
        MatchResultEventDto result = new MatchResultEventDto(bet.getMatchId(), 3, 1);

        bet.settle(result);

        assertThat(bet.getHomeGoals()).isEqualTo(3);
        assertThat(bet.getAwayGoals()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should set status to WON and store goals when HOME_WIN and home wins")
    void should_setStatusWonAndGoals_when_homeWinBetAndHomeWins() {
        Bet bet = buildBet(BetType.HOME_WIN);
        MatchResultEventDto result = new MatchResultEventDto(bet.getMatchId(), 2, 0);

        bet.settle(result);

        assertThat(bet.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(bet.getHomeGoals()).isEqualTo(2);
        assertThat(bet.getAwayGoals()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should set status to LOST and store goals when HOME_WIN but away wins")
    void should_setStatusLostAndGoals_when_homeWinBetAndAwayWins() {
        Bet bet = buildBet(BetType.HOME_WIN);
        MatchResultEventDto result = new MatchResultEventDto(bet.getMatchId(), 0, 2);

        bet.settle(result);

        assertThat(bet.getStatus()).isEqualTo(BetStatus.LOST);
        assertThat(bet.getHomeGoals()).isEqualTo(0);
        assertThat(bet.getAwayGoals()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should store goals even on 0-0 draw")
    void should_storeZeroGoals_when_scorelessDraw() {
        Bet bet = buildBet(BetType.DRAW);
        MatchResultEventDto result = new MatchResultEventDto(bet.getMatchId(), 0, 0);

        bet.settle(result);

        assertThat(bet.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(bet.getHomeGoals()).isEqualTo(0);
        assertThat(bet.getAwayGoals()).isEqualTo(0);
    }
}

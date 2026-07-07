package com.poorbet.couponservice.mapper;

import com.poorbet.couponservice.domain.*;
import com.poorbet.couponservice.dto.BetDto;
import com.poorbet.couponservice.dto.CouponDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CouponMapper Unit Tests")
class CouponMapperTest {

    private final CouponMapper mapper = new CouponMapper();

    private Bet buildBet(Integer homeGoals, Integer awayGoals) {
        return Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .homeTeamName("Home FC")
                .awayTeamName("Away FC")
                .matchStartTime(OffsetDateTime.now())
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(2.0))
                .homeGoals(homeGoals)
                .awayGoals(awayGoals)
                .build();
    }

    private Coupon buildCoupon(List<Bet> bets) {
        Coupon coupon = Coupon.builder()
                .id(UUID.randomUUID())
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .potentialPayout(BigDecimal.valueOf(20.0))
                .userId(UUID.randomUUID())
                .reservationId(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .build();
        bets.forEach(coupon::addBet);
        return coupon;
    }

    @Test
    @DisplayName("Should map homeGoals and awayGoals when bet has a result")
    void should_mapHomeAndAwayGoals_when_betHasResult() {
        Bet bet = buildBet(3, 1);
        Coupon coupon = buildCoupon(List.of(bet));

        CouponDetailDto dto = mapper.toDetailDto(coupon);

        BetDto betDto = dto.bets().getFirst();
        assertThat(betDto.homeGoals()).isEqualTo(3);
        assertThat(betDto.awayGoals()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should map null goals when bet has no result yet")
    void should_mapNullGoals_when_betHasNoResult() {
        Bet bet = buildBet(null, null);
        Coupon coupon = buildCoupon(List.of(bet));

        CouponDetailDto dto = mapper.toDetailDto(coupon);

        BetDto betDto = dto.bets().getFirst();
        assertThat(betDto.homeGoals()).isNull();
        assertThat(betDto.awayGoals()).isNull();
    }

    @Test
    @DisplayName("Should calculate total odds as product of all bet odds")
    void should_calculateTotalOdds_as_productOfAllBetOdds() {
        Bet bet1 = buildBet(null, null);
        bet1.setOdds(new BigDecimal("2.00"));
        Bet bet2 = buildBet(null, null);
        bet2.setOdds(new BigDecimal("3.00"));
        Coupon coupon = buildCoupon(List.of(bet1, bet2));

        CouponDetailDto dto = mapper.toDetailDto(coupon);

        assertThat(dto.totalOdds()).isEqualByComparingTo("6.00");
    }

    @Test
    @DisplayName("Should preserve bet status in DTO")
    void should_preserveBetStatus_in_dto() {
        Bet bet = buildBet(2, 0);
        bet.setStatus(BetStatus.WON);
        Coupon coupon = buildCoupon(List.of(bet));

        CouponDetailDto dto = mapper.toDetailDto(coupon);

        assertThat(dto.bets().getFirst().status()).isEqualTo(BetStatus.WON);
    }
}

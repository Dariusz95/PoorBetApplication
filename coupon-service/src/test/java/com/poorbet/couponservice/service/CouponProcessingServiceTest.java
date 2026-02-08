package com.poorbet.couponservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.BetType;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.MatchResultDto;
import com.poorbet.couponservice.dto.MatchesFinishedEvent;
import com.poorbet.couponservice.repository.BetRepository;
import com.poorbet.couponservice.repository.CouponRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponProcessingService Unit Tests")
class CouponProcessingServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponProcessingService couponProcessingService;

    private UUID matchId1;
    private UUID matchId2;
    private UUID couponId1;

    @BeforeEach
    void setUp() {
        matchId1 = UUID.randomUUID();
        matchId2 = UUID.randomUUID();
        couponId1 = UUID.randomUUID();
    }

    private Bet createBet(UUID matchId, BetType betType, BetStatus status, Coupon coupon) {
        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId)
                .betType(betType)
                .status(status)
                .odds(BigDecimal.valueOf(1.5))
                .coupon(coupon)
                .build();
        return bet;
    }

    private Coupon createCoupon(UUID couponId, List<Bet> bets) {
        Coupon coupon = Coupon.builder()
                .id(couponId)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .bets(bets)
                .build();
        return coupon;
    }

    @Test
    @DisplayName("Should update coupon status to LOST when any bet is lost")
    void shouldUpdateCouponStatusToLostWhenAnyBetIsLost() {
        // Arrange
        Coupon coupon = createCoupon(couponId1, new ArrayList<>());
        Bet bet1 = createBet(matchId1, BetType.HOME_WIN, BetStatus.WON, coupon);
        Bet bet2 = createBet(matchId2, BetType.DRAW, BetStatus.LOST, coupon);
        coupon.setBets(Arrays.asList(bet1, bet2));

        // Act
        couponProcessingService.updateCouponStatus(coupon);

        // Assert
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.LOST);
    }

    @Test
    @DisplayName("Should update coupon status to WON when all bets are won")
    void shouldUpdateCouponStatusToWonWhenAllBetsAreWon() {
        // Arrange
        Coupon coupon = createCoupon(couponId1, new ArrayList<>());
        Bet bet1 = createBet(matchId1, BetType.HOME_WIN, BetStatus.WON, coupon);
        Bet bet2 = createBet(matchId2, BetType.DRAW, BetStatus.WON, coupon);
        coupon.setBets(Arrays.asList(bet1, bet2));

        // Act
        couponProcessingService.updateCouponStatus(coupon);

        // Assert
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.WON);
    }

    @Test
    @DisplayName("Should keep coupon status unchanged when some bets are pending")
    void shouldKeepCouponStatusUnchangedWhenSomeBetsPending() {
        // Arrange
        Coupon coupon = createCoupon(couponId1, new ArrayList<>());
        Bet bet1 = createBet(matchId1, BetType.HOME_WIN, BetStatus.PENDING, coupon);
        Bet bet2 = createBet(matchId2, BetType.DRAW, BetStatus.WON, coupon);
        coupon.setBets(Arrays.asList(bet1, bet2));

        // Act
        couponProcessingService.updateCouponStatus(coupon);

        // Assert
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.OPEN);
    }

    @Test
    @DisplayName("Should handle coupon with single bet - WON")
    void shouldHandleSingleBetCouponWon() {
        // Arrange
        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .build();

        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.HOME_WIN)
                .status(BetStatus.WON)
                .odds(BigDecimal.valueOf(1.5))
                .coupon(coupon)
                .build();

        coupon.setBets(Collections.singletonList(bet));

        // Act
        couponProcessingService.updateCouponStatus(coupon);

        // Assert
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.WON);
    }

    @Test
    @DisplayName("Should handle coupon with single bet - LOST")
    void shouldHandleSingleBetCouponLost() {
        // Arrange
        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .build();

        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.HOME_WIN)
                .status(BetStatus.LOST)
                .odds(BigDecimal.valueOf(1.5))
                .coupon(coupon)
                .build();

        coupon.setBets(Collections.singletonList(bet));

        // Act
        couponProcessingService.updateCouponStatus(coupon);

        // Assert
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.LOST);
    }

    @Test
    @DisplayName("Should process finished matches event and update bet statuses")
    void shouldProcessFinishedMatchesEventAndUpdateBetStatuses() {
        // Arrange
        MatchResultDto result1 = MatchResultDto.builder()
                .id(matchId1)
                .homeGoals(2)
                .awayGoals(1)
                .build();

        MatchResultDto result2 = MatchResultDto.builder()
                .id(matchId2)
                .homeGoals(1)
                .awayGoals(1)
                .build();

        MatchesFinishedEvent event = new MatchesFinishedEvent(Arrays.asList(result1, result2));

        Bet bet1 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(1.5))
                .build();

        Bet bet2 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId2)
                .betType(BetType.DRAW)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(3.0))
                .build();

        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .bets(Arrays.asList(bet1, bet2))
                .build();

        bet1.setCoupon(coupon);
        bet2.setCoupon(coupon);

        when(betRepository.findAllByMatchIdIn(anySet())).thenReturn(Arrays.asList(bet1, bet2));
        when(couponRepository.findAllWithBetsByIds(anySet())).thenReturn(Collections.singletonList(coupon));

        // Act
        couponProcessingService.processFinishedMatch(event);

        // Assert
        assertThat(bet1.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(bet2.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.WON);
    }

    @Test
    @DisplayName("Should handle empty match results")
    void shouldHandleEmptyMatchResults() {
        // Arrange
        MatchesFinishedEvent event = new MatchesFinishedEvent(Collections.emptyList());
        when(betRepository.findAllByMatchIdIn(anySet())).thenReturn(Collections.emptyList());

        // Act & Assert - should not throw exception
        assertThatCode(() -> couponProcessingService.processFinishedMatch(event))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should extract unique coupon IDs from bets")
    void shouldExtractUniqueCouponIdsFromBets() {
        // Arrange
        MatchResultDto result1 = MatchResultDto.builder()
                .id(matchId1)
                .homeGoals(2)
                .awayGoals(1)
                .build();

        MatchesFinishedEvent event = new MatchesFinishedEvent(Collections.singletonList(result1));

        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .build();

        Bet bet1 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(1.5))
                .coupon(coupon)
                .build();

        Bet bet2 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.DRAW)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(3.0))
                .coupon(coupon)
                .build();

        when(betRepository.findAllByMatchIdIn(anySet())).thenReturn(Arrays.asList(bet1, bet2));
        when(couponRepository.findAllWithBetsByIds(anySet())).thenReturn(Collections.singletonList(coupon));

        // Act
        couponProcessingService.processFinishedMatch(event);

        // Assert - should query with single coupon ID despite multiple bets
        verify(couponRepository, times(1)).findAllWithBetsByIds(argThat(ids -> ids.size() == 1 && ids.contains(couponId1)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    @DisplayName("Should handle multiple coupons with different number of bets")
    void shouldHandleMultipleCouponsWithDifferentBets(int numberOfBets) {
        // Arrange
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Coupon coupon = Coupon.builder()
                    .id(UUID.randomUUID())
                    .stake(BigDecimal.TEN)
                    .status(CouponStatus.OPEN)
                    .build();
            coupons.add(coupon);
        }

        MatchResultDto result = MatchResultDto.builder()
                .id(matchId1)
                .homeGoals(2)
                .awayGoals(1)
                .build();

        MatchesFinishedEvent event = new MatchesFinishedEvent(Collections.singletonList(result));

        when(betRepository.findAllByMatchIdIn(anySet())).thenReturn(Collections.emptyList());
        when(couponRepository.findAllWithBetsByIds(anySet())).thenReturn(coupons);

        // Act
        couponProcessingService.processFinishedMatch(event);

        // Assert
        verify(couponRepository).findAllWithBetsByIds(any());
    }

    @Test
    @DisplayName("Should handle HOME_WIN bet correctly")
    void shouldHandleHomeWinBetCorrectly() {
        // Arrange
        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .build();

        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(1.5))
                .coupon(coupon)
                .build();

        coupon.setBets(Collections.singletonList(bet));

        MatchResultDto result = MatchResultDto.builder()
                .id(matchId1)
                .homeGoals(3)
                .awayGoals(1)
                .build();

        MatchesFinishedEvent event = new MatchesFinishedEvent(Collections.singletonList(result));

        when(betRepository.findAllByMatchIdIn(Set.of(matchId1))).thenReturn(Collections.singletonList(bet));
        when(couponRepository.findAllWithBetsByIds(Set.of(couponId1))).thenReturn(Collections.singletonList(coupon));

        // Act
        couponProcessingService.processFinishedMatch(event);

        // Assert
        assertThat(bet.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.WON);
    }

    @Test
    @DisplayName("Should handle DRAW bet correctly")
    void shouldHandleDrawBetCorrectly() {
        // Arrange
        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .build();

        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.DRAW)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(3.0))
                .coupon(coupon)
                .build();

        coupon.setBets(Collections.singletonList(bet));

        MatchResultDto result = MatchResultDto.builder()
                .id(matchId1)
                .homeGoals(1)
                .awayGoals(1)
                .build();

        MatchesFinishedEvent event = new MatchesFinishedEvent(Collections.singletonList(result));

        when(betRepository.findAllByMatchIdIn(Set.of(matchId1))).thenReturn(Collections.singletonList(bet));
        when(couponRepository.findAllWithBetsByIds(Set.of(couponId1))).thenReturn(Collections.singletonList(coupon));

        // Act
        couponProcessingService.processFinishedMatch(event);

        // Assert
        assertThat(bet.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.WON);
    }

    @Test
    @DisplayName("Should handle AWAY_WIN bet correctly")
    void shouldHandleAwayWinBetCorrectly() {
        // Arrange
        Coupon coupon = Coupon.builder()
                .id(couponId1)
                .stake(BigDecimal.TEN)
                .status(CouponStatus.OPEN)
                .build();

        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(matchId1)
                .betType(BetType.AWAY_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(2.0))
                .coupon(coupon)
                .build();

        coupon.setBets(Collections.singletonList(bet));

        MatchResultDto result = MatchResultDto.builder()
                .id(matchId1)
                .homeGoals(1)
                .awayGoals(3)
                .build();

        MatchesFinishedEvent event = new MatchesFinishedEvent(Collections.singletonList(result));

        when(betRepository.findAllByMatchIdIn(Set.of(matchId1))).thenReturn(Collections.singletonList(bet));
        when(couponRepository.findAllWithBetsByIds(Set.of(couponId1))).thenReturn(Collections.singletonList(coupon));

        // Act
        couponProcessingService.processFinishedMatch(event);

        // Assert
        assertThat(bet.getStatus()).isEqualTo(BetStatus.WON);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.WON);
    }
}

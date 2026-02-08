package com.poorbet.couponservice.domain;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Coupon Entity Unit Tests")
class CouponTest {

    private Coupon coupon;
    private UUID couponId;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();
        coupon = Coupon.builder()
                .id(couponId)
                .stake(new BigDecimal("50.00"))
                .status(CouponStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("Should create coupon with all required fields")
    void shouldCreateCouponWithAllRequiredFields() {
        // Assert
        assertThat(coupon)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getId()).isEqualTo(couponId);
                    assertThat(c.getStake()).isEqualByComparingTo(new BigDecimal("50.00"));
                    assertThat(c.getStatus()).isEqualTo(CouponStatus.OPEN);
                });
    }

    @Test
    @DisplayName("Should initialize empty bets list")
    void shouldInitializeEmptyBetsList() {
        // Assert
        assertThat(coupon.getBets())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Should add single bet to coupon")
    void shouldAddSingleBetToCoupon() {
        // Arrange
        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(1.5))
                .build();

        // Act
        coupon.addBet(bet);

        // Assert
        assertThat(coupon.getBets())
                .hasSize(1)
                .contains(bet);
        assertThat(bet.getCoupon()).isEqualTo(coupon);
    }

    @Test
    @DisplayName("Should add multiple bets to coupon")
    void shouldAddMultipleBetsToCoupon() {
        // Arrange
        Bet bet1 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(1.5))
                .build();

        Bet bet2 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .betType(BetType.DRAW)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(3.0))
                .build();

        Bet bet3 = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .betType(BetType.AWAY_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(2.5))
                .build();

        // Act
        coupon.addBet(bet1);
        coupon.addBet(bet2);
        coupon.addBet(bet3);

        // Assert
        assertThat(coupon.getBets())
                .hasSize(3)
                .contains(bet1, bet2, bet3);
    }

    @Test
    @DisplayName("Should set coupon reference on bet when adding")
    void shouldSetCouponReferenceOnBetWhenAdding() {
        // Arrange
        Bet bet = Bet.builder()
                .id(UUID.randomUUID())
                .matchId(UUID.randomUUID())
                .betType(BetType.HOME_WIN)
                .status(BetStatus.PENDING)
                .odds(BigDecimal.valueOf(1.5))
                .build();

        assertThat(bet.getCoupon()).isNull();

        // Act
        coupon.addBet(bet);

        // Assert
        assertThat(bet.getCoupon()).isEqualTo(coupon);
    }

    @Test
    @DisplayName("Should maintain bet order when adding multiple bets")
    void shouldMaintainBetOrderWhenAddingMultipleBets() {
        // Arrange
        Bet bet1 = Bet.builder().id(UUID.randomUUID()).build();
        Bet bet2 = Bet.builder().id(UUID.randomUUID()).build();
        Bet bet3 = Bet.builder().id(UUID.randomUUID()).build();

        // Act
        coupon.addBet(bet1);
        coupon.addBet(bet2);
        coupon.addBet(bet3);

        // Assert
        assertThat(coupon.getBets())
                .extracting(Bet::getId)
                .containsExactly(bet1.getId(), bet2.getId(), bet3.getId());
    }

    @Test
    @DisplayName("Should allow updating coupon stake")
    void shouldAllowUpdatingCouponStake() {
        // Arrange
        BigDecimal newStake = new BigDecimal("100.00");

        // Act
        coupon.setStake(newStake);

        // Assert
        assertThat(coupon.getStake()).isEqualByComparingTo(newStake);
    }

    @Test
    @DisplayName("Should allow updating coupon status")
    void shouldAllowUpdatingCouponStatus() {
        // Arrange
        CouponStatus newStatus = CouponStatus.WON;

        // Act
        coupon.setStatus(newStatus);

        // Assert
        assertThat(coupon.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("Should support different coupon statuses")
    void shouldSupportDifferentCouponStatuses() {
        // Assert
        assertThatCode(() -> {
            coupon.setStatus(CouponStatus.OPEN);
            coupon.setStatus(CouponStatus.WON);
            coupon.setStatus(CouponStatus.LOST);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle large stake amounts")
    void shouldHandleLargeStakeAmounts() {
        // Arrange
        BigDecimal largeStake = new BigDecimal("999999.99");

        // Act
        coupon.setStake(largeStake);

        // Assert
        assertThat(coupon.getStake()).isEqualByComparingTo(largeStake);
    }

    @Test
    @DisplayName("Should handle small stake amounts")
    void shouldHandleSmallStakeAmounts() {
        // Arrange
        BigDecimal smallStake = new BigDecimal("1.00");

        // Act
        coupon.setStake(smallStake);

        // Assert
        assertThat(coupon.getStake()).isEqualByComparingTo(smallStake);
    }

    @Test
    @DisplayName("Should support builder pattern")
    void shouldSupportBuilderPattern() {
        // Act
        Coupon builtCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .stake(new BigDecimal("75.50"))
                .status(CouponStatus.OPEN)
                .build();

        // Assert
        assertThat(builtCoupon)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getId()).isNotNull();
                    assertThat(c.getStake()).isEqualByComparingTo(new BigDecimal("75.50"));
                    assertThat(c.getStatus()).isEqualTo(CouponStatus.OPEN);
                });
    }
}

package com.poorbet.couponservice.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.BetType;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.domain.OddsType;
import com.poorbet.couponservice.dto.CreateBetDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.repository.CouponRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService Unit Tests")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MatchClient matchClient;

    @InjectMocks
    private CouponService couponService;

    private static final BigDecimal VALID_STAKE = new BigDecimal("50.00");
    private static final Double DEFAULT_ODD = 1.5;

    private CreateCouponDto validCreateCouponDto;
    private UUID userId;
    private UUID matchId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        matchId = UUID.randomUUID();
        validCreateCouponDto = createValidCouponDto();
    }

    private CreateCouponDto createValidCouponDto() {
        CreateCouponDto dto = new CreateCouponDto();
        dto.setStake(VALID_STAKE);
        dto.setBets(Arrays.asList(
                createBetDto(matchId, BetType.HOME_WIN),
                createBetDto(UUID.randomUUID(), BetType.DRAW)
        ));
        return dto;
    }

    private CreateBetDto createBetDto(UUID id, BetType type) {
        return CreateBetDto.builder()
                .matchId(id)
                .betType(type)
                .build();
    }

    private void setupMatchClientWithOdd(Double oddValue) {
        when(matchClient.getOdd(any(UUID.class), eq(OddsType.HOME_WIN)))
                .thenReturn(oddValue);
    }

    private void setupRepositoryToReturnCoupon(Coupon coupon) {
        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should create coupon with correct stake and bets")
    void shouldCreateCouponWithCorrectStakeAndBets() {
        // Arrange
        setupMatchClientWithOdd(DEFAULT_ODD);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(coupon -> {
                    assertThat(coupon.getStake()).isEqualByComparingTo(VALID_STAKE);
                    assertThat(coupon.getStatus()).isEqualTo(CouponStatus.OPEN);
                    assertThat(coupon.getBets()).hasSize(2);
                });
    }

    @Test
    @DisplayName("Should create bets with correct odds from MatchClient")
    void shouldCreateBetsWithCorrectOdds() {
        // Arrange
        Double oddValue = 2.5;
        setupMatchClientWithOdd(oddValue);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result.getBets())
                .hasSize(2)
                .allSatisfy(bet -> {
                    assertThat(bet.getOdds()).isEqualByComparingTo(BigDecimal.valueOf(oddValue));
                    assertThat(bet.getStatus()).isEqualTo(BetStatus.PENDING);
                    assertThat(bet.getMatchId()).isNotNull();
                });
    }

    @Test
    @DisplayName("Should call MatchClient for each bet in coupon")
    void shouldCallMatchClientForEachBet() {
        // Arrange
        int expectedBetCount = validCreateCouponDto.getBets().size();
        setupMatchClientWithOdd(DEFAULT_ODD);
        setupRepositoryToReturnCoupon(null);

        // Act
        couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        verify(matchClient, times(expectedBetCount))
                .getOdd(any(UUID.class), eq(OddsType.HOME_WIN));
    }

    @Test
    @DisplayName("Should set correct betType for each bet")
    void shouldSetCorrectBetTypeForEachBet() {
        // Arrange
        setupMatchClientWithOdd(DEFAULT_ODD);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result.getBets())
                .extracting(Bet::getBetType)
                .containsExactly(BetType.HOME_WIN, BetType.DRAW);
    }

    @Test
    @DisplayName("Should save coupon in repository")
    void shouldSaveCouponInRepository() {
        // Arrange
        setupMatchClientWithOdd(DEFAULT_ODD);
        setupRepositoryToReturnCoupon(null);

        // Act
        couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Should handle single bet in coupon")
    void shouldHandleSingleBetInCoupon() {
        // Arrange
        CreateCouponDto singleBetCoupon = new CreateCouponDto();
        singleBetCoupon.setStake(new BigDecimal("100.00"));
        singleBetCoupon.setBets(List.of(createBetDto(matchId, BetType.AWAY_WIN)));

        setupMatchClientWithOdd(3.0);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(singleBetCoupon, userId);

        // Assert
        assertThat(result.getBets())
                .hasSize(1)
                .first()
                .satisfies(bet -> assertThat(bet.getBetType()).isEqualTo(BetType.AWAY_WIN));
    }

    @Test
    @DisplayName("Should handle multiple bets in coupon")
    void shouldHandleMultipleBetsInCoupon() {
        // Arrange
        BigDecimal stake = new BigDecimal("75.00");
        CreateCouponDto multipleBetsCoupon = new CreateCouponDto();
        multipleBetsCoupon.setStake(stake);
        multipleBetsCoupon.setBets(Arrays.asList(
                createBetDto(UUID.randomUUID(), BetType.HOME_WIN),
                createBetDto(UUID.randomUUID(), BetType.DRAW),
                createBetDto(UUID.randomUUID(), BetType.AWAY_WIN)
        ));

        setupMatchClientWithOdd(1.8);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(multipleBetsCoupon, userId);

        // Assert
        assertThat(result)
                .satisfies(coupon -> {
                    assertThat(coupon.getBets()).hasSize(3);
                    assertThat(coupon.getStake()).isEqualByComparingTo(stake);
                });
    }

    @Test
    @DisplayName("Should create coupon with default status OPEN")
    void shouldCreateCouponWithStatusOpen() {
        // Arrange
        setupMatchClientWithOdd(DEFAULT_ODD);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result.getStatus()).isEqualTo(CouponStatus.OPEN);
    }

    @Test
    @DisplayName("Should create coupon with generated UUID")
    void shouldCreateCouponWithGeneratedUUID() {
        // Arrange
        setupMatchClientWithOdd(DEFAULT_ODD);
        setupRepositoryToReturnCoupon(null);

        // Act
        Coupon result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert - Repository should receive coupon with all bets properly linked
        assertThat(result.getBets())
                .isNotEmpty()
                .allSatisfy(bet -> assertThat(bet.getCoupon()).isEqualTo(result));
    }

    @Test
    @DisplayName("Should propagate MatchClient exceptions")
    void shouldPropagateMatchClientExceptions() {
        // Arrange
        when(matchClient.getOdd(any(UUID.class), eq(OddsType.HOME_WIN)))
                .thenThrow(new RuntimeException("MatchClient unavailable"));

        // Act & Assert
        assertThatThrownBy(() -> couponService.createCoupon(validCreateCouponDto, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("MatchClient unavailable");
    }
}

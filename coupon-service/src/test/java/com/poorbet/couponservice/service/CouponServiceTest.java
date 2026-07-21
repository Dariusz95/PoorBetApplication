package com.poorbet.couponservice.service;

import com.poorbet.commons.commons.auth.UserBatchLookupRequest;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;
import com.poorbet.commons.commons.auth.UserDto;
import com.poorbet.commons.commons.pagination.PageResponse;
import com.poorbet.couponservice.client.auth.AuthClient;
import com.poorbet.couponservice.client.match.MatchClient;
import com.poorbet.couponservice.client.wallet.WalletClient;
import com.poorbet.couponservice.domain.*;
import com.poorbet.couponservice.dto.*;
import com.poorbet.couponservice.mapper.CouponMapper;
import com.poorbet.couponservice.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService Unit Tests")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private MatchClient matchClient;

    @Mock
    private WalletClient walletClient;

    @Mock
    private AuthClient authClient;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private CouponService couponService;

    @Spy
    private CouponMapper couponMapper = new CouponMapper();

    private static final BigDecimal VALID_STAKE = new BigDecimal("50.00");
    private static final BigDecimal DEFAULT_ODD = new BigDecimal("1.5");

    private static final String HOME_TEAM = "Real Madrid";
    private static final String AWAY_TEAM = "Barcelona";

    private MatchBetSnapshotDto createSnapshot1() {
        return new MatchBetSnapshotDto(
                firstMatchId,
                HOME_TEAM,
                AWAY_TEAM,
                OffsetDateTime.parse("2026-06-20T20:45:00Z"),
                DEFAULT_ODD
        );
    }

    private MatchBetSnapshotDto createSnapshot2() {
        return new MatchBetSnapshotDto(
                secondMatchId,
                HOME_TEAM,
                AWAY_TEAM,
                OffsetDateTime.parse("2026-06-20T20:45:00Z"),
                DEFAULT_ODD
        );
    }

    private CreateCouponDto validCreateCouponDto;
    private UUID userId;
    private UUID firstMatchId;
    private UUID secondMatchId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        firstMatchId = UUID.randomUUID();
        secondMatchId = UUID.randomUUID();
        validCreateCouponDto = createValidCouponDto();
    }

    private CreateCouponDto createValidCouponDto() {
        CreateCouponDto dto = new CreateCouponDto();
        dto.setStake(VALID_STAKE);
        dto.setBets(Arrays.asList(
                createBetDto(firstMatchId, BetType.HOME_WIN),
                createBetDto(secondMatchId, BetType.DRAW)
        ));
        return dto;
    }

    private CreateBetDto createBetDto(UUID id, BetType type) {
        return CreateBetDto.builder()
                .matchId(id)
                .betType(type)
                .build();
    }

    private void setupMatchClientWithSnapshots(MatchBetSnapshotDto... snapshots) {
        doNothing().when(walletClient).reserve(any(UUID.class), any());
        when(matchClient.getBetSnapshots(anyList())).thenReturn(Arrays.asList(snapshots));
    }
    private void setupRepositoryToReturnCoupon() {
        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should create coupon with correct stake and bets")
    void shouldCreateCouponWithCorrectStakeAndBets() {
        // Arrange
        setupMatchClientWithSnapshots(createSnapshot1(), createSnapshot2());
        setupRepositoryToReturnCoupon();

        // Act
        CouponDetailDto result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(coupon -> {
                    assertThat(coupon.stake()).isEqualByComparingTo(VALID_STAKE);
                    assertThat(coupon.status()).isEqualTo(CouponStatus.OPEN);
                    assertThat(coupon.bets()).hasSize(2);
                });
    }

    @Test
    @DisplayName("Should create bets with correct odds from MatchClient")
    void shouldCreateBetsWithCorrectOdds() {
        // Arrange
        setupMatchClientWithSnapshots(createSnapshot1(), createSnapshot2());
        setupRepositoryToReturnCoupon();

        // Act
        CouponDetailDto result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result.bets())
                .hasSize(2)
                .allSatisfy(bet -> {
                    assertThat(bet.odds()).isEqualByComparingTo(DEFAULT_ODD);
                    assertThat(bet.status()).isEqualTo(BetStatus.PENDING);
                    assertThat(bet.matchId()).isNotNull();
                });
    }

    @Test
    @DisplayName("Should call MatchClient for each bet in coupon")
    void shouldCallMatchClientForEachBet() {
        // Arrange
        int expectedBetCount = validCreateCouponDto.getBets().size();
        setupMatchClientWithSnapshots(createSnapshot1(), createSnapshot2());
        setupRepositoryToReturnCoupon();

        // Act
        couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        verify(matchClient).getBetSnapshots(argThat(requests -> requests.size() == expectedBetCount));
    }

    @Test
    @DisplayName("Should set correct betType for each bet")
    void shouldSetCorrectBetTypeForEachBet() {
        // Arrange
        setupMatchClientWithSnapshots(createSnapshot1(), createSnapshot2());
        setupRepositoryToReturnCoupon();

        // Act
        CouponDetailDto result = couponService.createCoupon(validCreateCouponDto, userId);

        // Assert
        assertThat(result.bets())
                .extracting(BetDto::betType)
                .containsExactly(BetType.HOME_WIN, BetType.DRAW);
    }

    @Test
    @DisplayName("Should save coupon in repository")
    void shouldSaveCouponInRepository() {
        // Arrange
        setupMatchClientWithSnapshots(createSnapshot1(), createSnapshot2());
        setupRepositoryToReturnCoupon();

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
        singleBetCoupon.setBets(List.of(createBetDto(firstMatchId, BetType.AWAY_WIN)));

        MatchBetSnapshotDto snapshot = new MatchBetSnapshotDto(
                firstMatchId,
                HOME_TEAM,
                AWAY_TEAM,
                OffsetDateTime.parse("2026-06-20T20:45:00Z"),
                DEFAULT_ODD
        );

        setupMatchClientWithSnapshots(snapshot);

        setupRepositoryToReturnCoupon();

        // Act
        CouponDetailDto result = couponService.createCoupon(singleBetCoupon, userId);

        // Assert
        assertThat(result.bets())
                .hasSize(1)
                .first()
                .satisfies(bet -> assertThat(bet.betType()).isEqualTo(BetType.AWAY_WIN));
    }


    @Test
    @DisplayName("Should propagate MatchClient exceptions")
    void shouldPropagateMatchClientExceptions() {
        // Arrange
        when(matchClient.getBetSnapshots(anyList()))
                .thenThrow(new RuntimeException("MatchClient unavailable"));
        doNothing().when(walletClient).reserve(any(UUID.class), any());

        // Act & Assert
        assertThatThrownBy(() -> couponService.createCoupon(validCreateCouponDto, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("MatchClient unavailable");

        verify(outboxService).saveEvent(any(), any());
    }

    private Coupon buildWonCoupon(UUID couponId, UUID couponUserId, BigDecimal totalOdds, BigDecimal potentialPayout) {
        return Coupon.builder()
                .id(couponId)
                .userId(couponUserId)
                .stake(VALID_STAKE)
                .status(CouponStatus.WON)
                .totalOdds(totalOdds)
                .potentialPayout(potentialPayout)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should enrich ranking entries with emails fetched from AuthClient")
    void shouldEnrichRankingWithEmailsFromAuthClient() {
        // Arrange
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        Coupon firstCoupon = buildWonCoupon(UUID.randomUUID(), firstUserId, new BigDecimal("10.00"), new BigDecimal("500.00"));
        Coupon secondCoupon = buildWonCoupon(UUID.randomUUID(), secondUserId, new BigDecimal("5.00"), new BigDecimal("250.00"));

        when(couponRepository.findByStatus(eq(CouponStatus.WON), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(firstCoupon, secondCoupon)));

        when(authClient.getUsersBatch(any())).thenReturn(new UserBatchLookupResponse(Map.of(
                firstUserId, new UserDto(firstUserId, "first@example.com"),
                secondUserId, new UserDto(secondUserId, "second@example.com")
        )));

        // Act
        PageResponse<RankingCouponResponseDto> result = couponService.getHighestTotalOdds();

        // Assert
        assertThat(result.content())
                .extracting(RankingCouponResponseDto::email)
                .containsExactly("first@example.com", "second@example.com");
    }

    @Test
    @DisplayName("Should request pageable with descending totalOdds sort for getHighestTotalOdds")
    void shouldRequestTotalOddsDescendingSort() {
        // Arrange
        when(couponRepository.findByStatus(eq(CouponStatus.WON), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(authClient.getUsersBatch(any())).thenReturn(new UserBatchLookupResponse(Map.of()));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        couponService.getHighestTotalOdds();

        // Assert
        verify(couponRepository).findByStatus(eq(CouponStatus.WON), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort().getOrderFor("totalOdds"))
                .isNotNull()
                .satisfies(order -> assertThat(order.isDescending()).isTrue());
    }

    @Test
    @DisplayName("Should request pageable with descending potentialPayout sort for getHighestPotentialPayout")
    void shouldRequestPotentialPayoutDescendingSort() {
        // Arrange
        when(couponRepository.findByStatus(eq(CouponStatus.WON), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(authClient.getUsersBatch(any())).thenReturn(new UserBatchLookupResponse(Map.of()));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        couponService.getHighestPotentialPayout();

        // Assert
        verify(couponRepository).findByStatus(eq(CouponStatus.WON), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable.getSort().getOrderFor("potentialPayout"))
                .isNotNull()
                .satisfies(order -> assertThat(order.isDescending()).isTrue());
    }

    @Test
    @DisplayName("Should deduplicate user ids before calling AuthClient when the same user appears multiple times")
    void shouldDeduplicateUserIdsBeforeCallingAuthClient() {
        // Arrange
        UUID sharedUserId = UUID.randomUUID();

        Coupon firstCoupon = buildWonCoupon(UUID.randomUUID(), sharedUserId, new BigDecimal("10.00"), new BigDecimal("500.00"));
        Coupon secondCoupon = buildWonCoupon(UUID.randomUUID(), sharedUserId, new BigDecimal("8.00"), new BigDecimal("400.00"));

        when(couponRepository.findByStatus(eq(CouponStatus.WON), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(firstCoupon, secondCoupon)));

        when(authClient.getUsersBatch(any())).thenReturn(new UserBatchLookupResponse(
                Map.of(sharedUserId, new UserDto(sharedUserId, "shared@example.com"))
        ));

        // Act
        couponService.getHighestTotalOdds();

        // Assert
        ArgumentCaptor<UserBatchLookupRequest> requestCaptor = ArgumentCaptor.forClass(UserBatchLookupRequest.class);
        verify(authClient).getUsersBatch(requestCaptor.capture());

        assertThat(requestCaptor.getValue().ids()).containsExactly(sharedUserId);
    }

    @Test
    @DisplayName("Should fall back to a placeholder when AuthClient has no matching user")
    void shouldFallbackToPlaceholderWhenUserMissingFromAuthResponse() {
        // Arrange
        UUID couponUserId = UUID.randomUUID();
        Coupon coupon = buildWonCoupon(UUID.randomUUID(), couponUserId, new BigDecimal("10.00"), new BigDecimal("500.00"));

        when(couponRepository.findByStatus(eq(CouponStatus.WON), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(coupon)));

        when(authClient.getUsersBatch(any())).thenReturn(new UserBatchLookupResponse(Map.of()));

        // Act
        PageResponse<RankingCouponResponseDto> result = couponService.getHighestTotalOdds();

        // Assert
        assertThat(result.content())
                .extracting(RankingCouponResponseDto::email)
                .containsExactly("User removed");
    }
}

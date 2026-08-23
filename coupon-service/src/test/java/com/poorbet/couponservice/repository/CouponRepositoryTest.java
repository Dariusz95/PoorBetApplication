package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.BetType;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CouponLiveViewDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class CouponRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("coupon_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private CouponRepository couponRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    private UUID userId;
    private UUID otherUserId;
    private UUID matchA;
    private UUID matchB;
    private UUID matchC;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();

        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        matchA = UUID.randomUUID();
        matchB = UUID.randomUUID();
        matchC = UUID.randomUUID();

        // userId / OPEN / bet on matchA
        couponRepository.save(couponWithBets(userId, CouponStatus.OPEN, bet(matchA)));

        // userId / OPEN / bets on matchB and matchC (matchC not queried)
        couponRepository.save(couponWithBets(userId, CouponStatus.OPEN, bet(matchB), bet(matchC)));

        // userId / WON / bet on matchA - should be excluded by status
        couponRepository.save(couponWithBets(userId, CouponStatus.WON, bet(matchA)));

        // otherUserId / OPEN / bet on matchA - should be excluded by user
        couponRepository.save(couponWithBets(otherUserId, CouponStatus.OPEN, bet(matchA)));
    }

    @Test
    void findLiveViewsByUserAndMatchIds_shouldReturnOnlyOpenCouponsForUserAndQueriedMatches() {
        // Act
        List<CouponLiveViewDto> result = couponRepository.findLiveViewsByUserAndMatchIds(
                userId, CouponStatus.OPEN, List.of(matchA, matchB)
        );

        // Assert
        assertThat(result)
                .extracting(CouponLiveViewDto::matchId)
                .containsExactlyInAnyOrder(matchA, matchB);
    }

    @Test
    void findLiveViewsByUserAndMatchIds_shouldReturnEmpty_whenNoCouponMatchesQueriedIds() {
        // Act
        List<CouponLiveViewDto> result = couponRepository.findLiveViewsByUserAndMatchIds(
                userId, CouponStatus.OPEN, List.of(UUID.randomUUID())
        );

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findLiveViewsByUserAndMatchIds_shouldReturnOneRowPerMatch_whenCouponCoversMultipleQueriedMatches() {
        // Arrange
        Coupon multiMatchCoupon = couponWithBets(userId, CouponStatus.OPEN, bet(matchA), bet(matchB));
        couponRepository.save(multiMatchCoupon);

        // Act
        List<CouponLiveViewDto> result = couponRepository.findLiveViewsByUserAndMatchIds(
                userId, CouponStatus.OPEN, List.of(matchA, matchB)
        );

        // Assert - the new coupon appears twice (once per matched bet), on top of the two seeded coupons
        assertThat(result)
                .filteredOn(view -> view.couponId().equals(multiMatchCoupon.getId()))
                .extracting(CouponLiveViewDto::matchId)
                .containsExactlyInAnyOrder(matchA, matchB);
    }

    @Test
    void findLiveViewsByUserAndMatchIds_shouldCollapseDuplicateRows_whenCouponHasTwoBetsOnSameMatch() {
        // Arrange - not possible to create via the API (BetSlipService enforces one bet per match
        // client-side), but nothing stops it at the DB/entity level, so the query must stay safe.
        Coupon duplicateBetCoupon = couponWithBets(userId, CouponStatus.OPEN, bet(matchA), bet(matchA));
        couponRepository.save(duplicateBetCoupon);

        // Act
        List<CouponLiveViewDto> result = couponRepository.findLiveViewsByUserAndMatchIds(
                userId, CouponStatus.OPEN, List.of(matchA)
        );

        // Assert
        assertThat(result)
                .filteredOn(view -> view.couponId().equals(duplicateBetCoupon.getId()))
                .hasSize(1)
                .extracting(CouponLiveViewDto::couponId, CouponLiveViewDto::matchId)
                .containsExactly(tuple(duplicateBetCoupon.getId(), matchA));
    }

    private Coupon couponWithBets(UUID userId, CouponStatus status, Bet... bets) {
        Coupon coupon = Coupon.builder()
                .userId(userId)
                .reservationId(UUID.randomUUID())
                .status(status)
                .stake(new BigDecimal("10.00"))
                .totalOdds(new BigDecimal("2.00"))
                .potentialPayout(new BigDecimal("20.00"))
                .createdAt(OffsetDateTime.now())
                .build();

        for (Bet bet : bets) {
            coupon.addBet(bet);
        }

        return coupon;
    }

    private Bet bet(UUID matchId) {
        return Bet.builder()
                .matchId(matchId)
                .homeTeamName("Home")
                .awayTeamName("Away")
                .matchStartTime(OffsetDateTime.now())
                .status(BetStatus.PENDING)
                .betType(BetType.HOME_WIN)
                .odds(new BigDecimal("2.00"))
                .build();
    }
}

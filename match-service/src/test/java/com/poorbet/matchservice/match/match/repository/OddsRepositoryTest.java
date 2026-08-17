package com.poorbet.matchservice.match.match.repository;

import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.domain.Odds;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.match.matchpool.repository.MatchPoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class OddsRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("odds_test")
            .withUsername("test")
            .withPassword("test");

    @MockitoBean
    private CacheManager cacheManager;

    @Autowired
    private OddsRepository oddsRepository;

    @Autowired
    private MatchPoolRepository matchPoolRepository;

    private UUID matchId;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.cache.type", () -> "none");
    }

    @BeforeEach
    void setUp() {
        MatchPool pool = MatchPool.builder()
                .status(PoolStatus.BETTABLE)
                .scheduledStartTime(OffsetDateTime.now().plusHours(1))
                .build();

        Match match = Match.builder()
                .homeTeamId(UUID.randomUUID())
                .awayTeamId(UUID.randomUUID())
                .status(MatchStatus.SCHEDULED)
                .build();

        Odds odds = Odds.builder()
                .homeWin(new BigDecimal("1.50"))
                .draw(new BigDecimal("3.25"))
                .awayWin(new BigDecimal("5.00"))
                .over25(new BigDecimal("1.80"))
                .under25(new BigDecimal("2.10"))
                .over35(new BigDecimal("3.40"))
                .under35(new BigDecimal("1.30"))
                .build();

        match.setOdds(odds);
        pool.addMatch(match);

        matchPoolRepository.saveAndFlush(pool);
        matchId = match.getId();
    }

    @Test
    void findByMatchId_shouldReturnOddsWithAllFields_whenPresent() {
        // Act
        Optional<Odds> result = oddsRepository.findByMatchId(matchId);

        // Assert
        assertTrue(result.isPresent());
        Odds odds = result.get();
        assertEquals(0, new BigDecimal("1.50").compareTo(odds.getHomeWin()));
        assertEquals(0, new BigDecimal("3.25").compareTo(odds.getDraw()));
        assertEquals(0, new BigDecimal("5.00").compareTo(odds.getAwayWin()));
        assertEquals(0, new BigDecimal("1.80").compareTo(odds.getOver25()));
        assertEquals(0, new BigDecimal("2.10").compareTo(odds.getUnder25()));
        assertEquals(0, new BigDecimal("3.40").compareTo(odds.getOver35()));
        assertEquals(0, new BigDecimal("1.30").compareTo(odds.getUnder35()));
    }

    @Test
    void findByMatchId_shouldReturnEmpty_whenMatchHasNoOdds() {
        // Act
        Optional<Odds> result = oddsRepository.findByMatchId(UUID.randomUUID());

        // Assert
        assertTrue(result.isEmpty());
    }
}

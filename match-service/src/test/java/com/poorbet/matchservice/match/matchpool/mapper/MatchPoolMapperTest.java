package com.poorbet.matchservice.match.matchpool.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.poorbet.matchservice.fixture.MatchFixtures;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.Odds;
import com.poorbet.matchservice.match.match.dto.response.MatchDto;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;

@DisplayName("MatchPoolMapper Unit Tests")
class MatchPoolMapperTest {

    private MatchPoolMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MatchPoolMapper();
    }

    @Nested
    @DisplayName("Mapping Single Pool")
    class MappingSinglePool {

        @Test
        @DisplayName("Should map pool with all required fields")
        void shouldMapPoolWithAllRequiredFields() {
            // Arrange
            UUID poolId = UUID.randomUUID();
            OffsetDateTime scheduledTime = OffsetDateTime.now();
            MatchPool pool = MatchPool.builder()
                    .id(poolId)
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(scheduledTime)
                    .matches(new ArrayList<>())
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            assertThat(result)
                    .isNotNull()
                    .hasSize(1);
            MatchPoolDto dto = result.get(0);
            assertThat(dto.id()).isEqualTo(poolId);
            assertThat(dto.status()).isEqualTo(PoolStatus.BETTABLE);
            assertThat(dto.scheduledStartTime()).isEqualTo(scheduledTime);
        }

        @Test
        @DisplayName("Should map pool without matches")
        void shouldMapPoolWithoutMatches() {
            // Arrange
            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(new ArrayList<>())
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).matches()).isEmpty();
        }

        @Test
        @DisplayName("Should map pool with single match")
        void shouldMapPoolWithSingleMatch() {
            // Arrange
            Match match = createTestMatch();
            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(List.of(match))
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).matches()).hasSize(1);
        }

        @Test
        @DisplayName("Should map pool with multiple matches")
        void shouldMapPoolWithMultipleMatches() {
            // Arrange
            List<Match> matches = List.of(
                    createTestMatch(),
                    createTestMatch(),
                    createTestMatch()
            );
            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.STARTED)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(matches)
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).matches()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Mapping Multiple Pools")
    class MappingMultiplePools {

        @Test
        @DisplayName("Should map multiple pools")
        void shouldMapMultiplePools() {
            // Arrange
            List<MatchPool> pools = List.of(
                    createTestPool(),
                    createTestPool(),
                    createTestPool()
            );

            // Act
            List<MatchPoolDto> result = mapper.toDto(pools);

            // Assert
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("Should preserve pool order")
        void shouldPreservePoolOrder() {
            // Arrange
            UUID poolId1 = UUID.randomUUID();
            UUID poolId2 = UUID.randomUUID();
            UUID poolId3 = UUID.randomUUID();

            List<MatchPool> pools = List.of(
                    MatchPool.builder().id(poolId1).status(PoolStatus.BETTABLE)
                            .scheduledStartTime(OffsetDateTime.now()).matches(new ArrayList<>()).build(),
                    MatchPool.builder().id(poolId2).status(PoolStatus.BETTABLE)
                            .scheduledStartTime(OffsetDateTime.now()).matches(new ArrayList<>()).build(),
                    MatchPool.builder().id(poolId3).status(PoolStatus.BETTABLE)
                            .scheduledStartTime(OffsetDateTime.now()).matches(new ArrayList<>()).build()
            );

            // Act
            List<MatchPoolDto> result = mapper.toDto(pools);

            // Assert
            assertThat(result)
                    .hasSize(3)
                    .extracting(MatchPoolDto::id)
                    .containsExactly(poolId1, poolId2, poolId3);
        }

        @Test
        @DisplayName("Should map pools with different statuses")
        void shouldMapPoolsWithDifferentStatuses() {
            // Arrange
            List<MatchPool> pools = List.of(
                    MatchPool.builder().id(UUID.randomUUID()).status(PoolStatus.BETTABLE)
                            .scheduledStartTime(OffsetDateTime.now()).matches(new ArrayList<>()).build(),
                    MatchPool.builder().id(UUID.randomUUID()).status(PoolStatus.STARTED)
                            .scheduledStartTime(OffsetDateTime.now()).matches(new ArrayList<>()).build(),
                    MatchPool.builder().id(UUID.randomUUID()).status(PoolStatus.FINISHED)
                            .scheduledStartTime(OffsetDateTime.now()).matches(new ArrayList<>()).build()
            );

            // Act
            List<MatchPoolDto> result = mapper.toDto(pools);

            // Assert
            assertThat(result)
                    .hasSize(3)
                    .extracting(MatchPoolDto::status)
                    .containsExactly(PoolStatus.BETTABLE, PoolStatus.STARTED, PoolStatus.FINISHED);
        }
    }

    @Nested
    @DisplayName("Mapping Empty Pools")
    class MappingEmptyPools {

        @Test
        @DisplayName("Should handle empty pool list")
        void shouldHandleEmptyPoolList() {
            // Act
            List<MatchPoolDto> result = mapper.toDto(Collections.emptyList());

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list for null input")
        void shouldHandleNullInput() {
            // Act & Assert
            assertThatThrownBy(() -> mapper.toDto(null))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Match Mapping Within Pool")
    class MatchMappingWithinPool {

        @Test
        @DisplayName("Should map match with null odds")
        void shouldMapMatchWithNullOdds() {
            // Arrange
            Match match = Match.builder()
                    .id(UUID.randomUUID())
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .odds(null)
                    .build();

            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(List.of(match))
                    .build();

            // Act & Assert
            assertThatThrownBy(() -> mapper.toDto(List.of(pool)))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should map match with valid odds")
        void shouldMapMatchWithValidOdds() {
            // Arrange
            Odds odds = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("1.50"))
                    .draw(new BigDecimal("3.25"))
                    .awayWin(new BigDecimal("5.00"))
                    .build();

            Match match = Match.builder()
                    .id(UUID.randomUUID())
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .odds(odds)
                    .build();

            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(List.of(match))
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            MatchDto matchDto = result.get(0).matches().get(0);
            assertThat(matchDto.odds()).isNotNull();
            assertThat(matchDto.odds().homeWin()).isEqualByComparingTo(new BigDecimal("1.50"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesAndSpecialScenarios {

        @Test
        @DisplayName("Should handle many pools")
        void shouldHandleManyPools() {
            // Arrange
            List<MatchPool> pools = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                pools.add(createTestPool());
            }

            // Act
            List<MatchPoolDto> result = mapper.toDto(pools);

            // Assert
            assertThat(result).hasSize(100);
        }

        @Test
        @DisplayName("Should handle pools with many matches")
        void shouldHandlePoolsWithManyMatches() {
            // Arrange
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                matches.add(createTestMatch());
            }

            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(matches)
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            assertThat(result.get(0).matches()).hasSize(50);
        }

        @Test
        @DisplayName("Should be idempotent")
        void shouldBeIdempotent() {
            // Arrange
            List<MatchPool> pools = List.of(createTestPool());

            // Act
            List<MatchPoolDto> result1 = mapper.toDto(pools);
            List<MatchPoolDto> result2 = mapper.toDto(pools);

            // Assert
            assertThat(result1).isEqualTo(result2);
        }

        @Test
        @DisplayName("Should preserve timestamp accuracy")
        void shouldPreserveTimestampAccuracy() {
            // Arrange
            OffsetDateTime exactTime = OffsetDateTime.now();
            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(exactTime)
                    .matches(new ArrayList<>())
                    .build();

            // Act
            List<MatchPoolDto> result = mapper.toDto(List.of(pool));

            // Assert
            assertThat(result.get(0).scheduledStartTime()).isEqualTo(exactTime);
        }
    }

    // Helper methods
    private MatchPool createTestPool() {
        return MatchFixtures.createMatchPool();
    }

    private Match createTestMatch() {
        Odds odds = MatchFixtures.createOdds();
        Match match = MatchFixtures.createMatch();
        match.setOdds(odds);
        return match;
    }
}

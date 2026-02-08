package com.poorbet.matchservice.match.matchpool.domain;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.poorbet.matchservice.fixture.MatchFixtures;
import com.poorbet.matchservice.match.match.domain.Match;


@DisplayName("MatchPool Entity Unit Tests")
class MatchPoolTest {

    private MatchPool matchPool;
    private UUID poolId;
    private OffsetDateTime scheduledTime;

    @BeforeEach
    void setUp() {
        poolId = UUID.randomUUID();
        scheduledTime = OffsetDateTime.now().plusHours(2);

        matchPool = MatchPool.builder()
                .id(poolId)
                .status(PoolStatus.BETTABLE)
                .scheduledStartTime(scheduledTime)
                .build();
    }

    @Nested
    @DisplayName("Pool Creation and Initialization")
    class PoolCreationAndInitialization {

        @Test
        @DisplayName("Should create pool with all required fields")
        void shouldCreatePoolWithAllRequiredFields() {
            // Assert
            assertThat(matchPool)
                    .isNotNull()
                    .satisfies(pool -> {
                        assertThat(pool.getId()).isEqualTo(poolId);
                        assertThat(pool.getStatus()).isEqualTo(PoolStatus.BETTABLE);
                        assertThat(pool.getScheduledStartTime()).isEqualTo(scheduledTime);
                    });
        }

        @Test
        @DisplayName("Should initialize empty matches list")
        void shouldInitializeEmptyMatchesList() {
            // Assert
            assertThat(matchPool.getMatches())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("Should support builder pattern")
        void shouldSupportBuilderPattern() {
            // Act
            MatchPool builtPool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.STARTED)
                    .scheduledStartTime(OffsetDateTime.now())
                    .build();

            // Assert
            assertThat(builtPool).isNotNull();
            assertThat(builtPool.getStatus()).isEqualTo(PoolStatus.STARTED);
        }

        @Test
        @DisplayName("Should support no-arg constructor")
        void shouldSupportNoArgConstructor() {
            // Act
            MatchPool pool = new MatchPool();

            // Assert
            assertThat(pool).isNotNull();
            assertThat(pool.getMatches()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Pool Status Management")
    class PoolStatusManagement {

        @Test
        @DisplayName("Should set pool status to BETTABLE initially")
        void shouldSetPoolStatusToBettableInitially() {
            // Assert
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.BETTABLE);
        }

        @Test
        @DisplayName("Should update pool status to STARTED")
        void shouldUpdatePoolStatusToStarted() {
            // Act
            matchPool.setStatus(PoolStatus.STARTED);

            // Assert
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.STARTED);
        }

        @Test
        @DisplayName("Should update pool status to FINISHED")
        void shouldUpdatePoolStatusToFinished() {
            // Act
            matchPool.setStatus(PoolStatus.FINISHED);

            // Assert
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.FINISHED);
        }

        @Test
        @DisplayName("Should support status transitions")
        void shouldSupportStatusTransitions() {
            // Act
            matchPool.setStatus(PoolStatus.BETTABLE);
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.BETTABLE);

            matchPool.setStatus(PoolStatus.STARTED);
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.STARTED);

            matchPool.setStatus(PoolStatus.FINISHED);
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.FINISHED);
        }

        @Test
        @DisplayName("Should handle all pool status values")
        void shouldHandleAllPoolStatusValues() {
            // Act & Assert
            for (PoolStatus status : PoolStatus.values()) {
                matchPool.setStatus(status);
                assertThat(matchPool.getStatus()).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("Scheduled Start Time Management")
    class ScheduledStartTimeManagement {

        @Test
        @DisplayName("Should maintain scheduled start time")
        void shouldMaintainScheduledStartTime() {
            // Assert
            assertThat(matchPool.getScheduledStartTime()).isEqualTo(scheduledTime);
        }

        @Test
        @DisplayName("Should update scheduled start time")
        void shouldUpdateScheduledStartTime() {
            // Arrange
            OffsetDateTime newTime = OffsetDateTime.now().plusHours(5);

            // Act
            matchPool.setScheduledStartTime(newTime);

            // Assert
            assertThat(matchPool.getScheduledStartTime()).isEqualTo(newTime);
        }

        @Test
        @DisplayName("Should handle past scheduled times")
        void shouldHandlePastScheduledTimes() {
            // Arrange
            OffsetDateTime pastTime = OffsetDateTime.now().minusHours(1);

            // Act
            matchPool.setScheduledStartTime(pastTime);

            // Assert
            assertThat(matchPool.getScheduledStartTime()).isEqualTo(pastTime);
        }

        @Test
        @DisplayName("Should handle future scheduled times")
        void shouldHandleFutureScheduledTimes() {
            // Arrange
            OffsetDateTime futureTime = OffsetDateTime.now().plusDays(30);

            // Act
            matchPool.setScheduledStartTime(futureTime);

            // Assert
            assertThat(matchPool.getScheduledStartTime()).isEqualTo(futureTime);
        }
    }

    @Nested
    @DisplayName("Match Management")
    class MatchManagement {

        @Test
        @DisplayName("Should add single match to pool")
        void shouldAddSingleMatchToPool() {
            // Arrange
            Match match = createTestMatch();

            // Act
            matchPool.addMatch(match);

            // Assert
            assertThat(matchPool.getMatches())
                    .hasSize(1)
                    .contains(match);
        }

        @Test
        @DisplayName("Should set pool reference on match when adding")
        void shouldSetPoolReferenceOnMatchWhenAdding() {
            // Arrange
            Match match = createTestMatch();

            // Act
            matchPool.addMatch(match);

            // Assert
            assertThat(match.getPool()).isEqualTo(matchPool);
        }

        @Test
        @DisplayName("Should add multiple matches to pool")
        void shouldAddMultipleMatchesToPool() {
            // Arrange
            Match match1 = createTestMatch();
            Match match2 = createTestMatch();
            Match match3 = createTestMatch();

            // Act
            matchPool.addMatch(match1);
            matchPool.addMatch(match2);
            matchPool.addMatch(match3);

            // Assert
            assertThat(matchPool.getMatches())
                    .hasSize(3)
                    .contains(match1, match2, match3);
        }

        @Test
        @DisplayName("Should maintain bidirectional relationship with matches")
        void shouldMaintainBidirectionalRelationshipWithMatches() {
            // Arrange
            Match match = createTestMatch();

            // Act
            matchPool.addMatch(match);

            // Assert
            assertThat(match.getPool()).isEqualTo(matchPool);
            assertThat(matchPool.getMatches()).contains(match);
        }

        @Test
        @DisplayName("Should maintain match order when adding multiple")
        void shouldMaintainMatchOrderWhenAddingMultiple() {
            // Arrange
            Match match1 = createTestMatch();
            Match match2 = createTestMatch();
            Match match3 = createTestMatch();

            // Act
            matchPool.addMatch(match1);
            matchPool.addMatch(match2);
            matchPool.addMatch(match3);

            // Assert
            assertThat(matchPool.getMatches())
                    .containsExactly(match1, match2, match3);
        }

        @Test
        @DisplayName("Should allow direct match list manipulation")
        void shouldAllowDirectMatchListManipulation() {
            // Arrange
            List<Match> matches = new ArrayList<>();
            Match match1 = createTestMatch();
            Match match2 = createTestMatch();
            matches.add(match1);
            matches.add(match2);

            // Act
            matchPool.setMatches(matches);

            // Assert
            assertThat(matchPool.getMatches()).hasSize(2);
        }

        @Test
        @DisplayName("Should handle large number of matches")
        void shouldHandleLargeNumberOfMatches() {
            // Act
            for (int i = 0; i < 100; i++) {
                matchPool.addMatch(createTestMatch());
            }

            // Assert
            assertThat(matchPool.getMatches()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("Pool Lifecycle")
    class PoolLifecycle {

        @Test
        @DisplayName("Should support complete pool lifecycle")
        void shouldSupportCompletePoolLifecycle() {
            // Arrange
            Match match = createTestMatch();

            // Act & Assert
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.BETTABLE);

            matchPool.addMatch(match);
            assertThat(matchPool.getMatches()).hasSize(1);

            matchPool.setStatus(PoolStatus.STARTED);
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.STARTED);

            matchPool.setStatus(PoolStatus.FINISHED);
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.FINISHED);
        }

        @Test
        @DisplayName("Should maintain data integrity through lifecycle")
        void shouldMaintainDataIntegrityThroughLifecycle() {
            // Arrange
            Match match = createTestMatch();
            UUID initialId = matchPool.getId();
            OffsetDateTime initialTime = matchPool.getScheduledStartTime();

            // Act
            matchPool.addMatch(match);
            matchPool.setStatus(PoolStatus.STARTED);
            matchPool.setStatus(PoolStatus.FINISHED);

            // Assert
            assertThat(matchPool.getId()).isEqualTo(initialId);
            assertThat(matchPool.getScheduledStartTime()).isEqualTo(initialTime);
            assertThat(matchPool.getMatches()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesAndSpecialScenarios {

        @Test
        @DisplayName("Should handle duplicate match additions")
        void shouldHandleDuplicateMatchAdditions() {
            // Arrange
            Match match = createTestMatch();

            // Act
            matchPool.addMatch(match);
            matchPool.addMatch(match);

            // Assert - Both additions should be processed
            assertThat(matchPool.getMatches()).hasSize(2);
        }

        @Test
        @DisplayName("Should preserve all matches with status changes")
        void shouldPreserveAllMatchesWithStatusChanges() {
            // Arrange
            Match match1 = createTestMatch();
            Match match2 = createTestMatch();

            matchPool.addMatch(match1);
            matchPool.addMatch(match2);

            // Act
            matchPool.setStatus(PoolStatus.STARTED);
            matchPool.setStatus(PoolStatus.FINISHED);

            // Assert
            assertThat(matchPool.getMatches()).hasSize(2);
        }

        @Test
        @DisplayName("Should handle pool with no matches")
        void shouldHandlePoolWithNoMatches() {
            // Assert
            assertThat(matchPool.getMatches()).isEmpty();
            assertThat(matchPool.getStatus()).isEqualTo(PoolStatus.BETTABLE);
        }

        @Test
        @DisplayName("Should handle builder with initial matches")
        void shouldHandleBuilderWithInitialMatches() {
            // Arrange
            List<Match> matches = List.of(
                    createTestMatch(),
                    createTestMatch()
            );

            // Act
            MatchPool pool = MatchPool.builder()
                    .id(UUID.randomUUID())
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(OffsetDateTime.now())
                    .matches(matches)
                    .build();

            // Assert
            assertThat(pool.getMatches()).hasSize(2);
        }

        @Test
        @DisplayName("Should clear matches by setting empty list")
        void shouldClearMatchesBySettingEmptyList() {
            // Arrange
            matchPool.addMatch(createTestMatch());
            matchPool.addMatch(createTestMatch());

            // Act
            matchPool.setMatches(new ArrayList<>());

            // Assert
            assertThat(matchPool.getMatches()).isEmpty();
        }
    }

    // Helper method
    private Match createTestMatch() {
        return MatchFixtures.createMatch();
    }
}

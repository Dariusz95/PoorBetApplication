package com.poorbet.matchservice.match.match.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@DisplayName("Match Entity Unit Tests")
class MatchTest {

    private Match match;
    private UUID matchId;
    private UUID homeTeamId;
    private UUID awayTeamId;

    @BeforeEach
    void setUp() {
        matchId = UUID.randomUUID();
        homeTeamId = UUID.randomUUID();
        awayTeamId = UUID.randomUUID();

        match = Match.builder()
                .id(matchId)
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .homeGoals(0)
                .awayGoals(0)
                .status(MatchStatus.SCHEDULED)
                .build();
    }

    @Nested
    @DisplayName("Match Creation and Initialization")
    class MatchCreationAndInitialization {

        @Test
        @DisplayName("Should create match with all required fields")
        void shouldCreateMatchWithAllRequiredFields() {
            // Assert
            assertThat(match)
                    .isNotNull()
                    .satisfies(m -> {
                        assertThat(m.getId()).isEqualTo(matchId);
                        assertThat(m.getHomeTeamId()).isEqualTo(homeTeamId);
                        assertThat(m.getAwayTeamId()).isEqualTo(awayTeamId);
                        assertThat(m.getStatus()).isEqualTo(MatchStatus.SCHEDULED);
                    });
        }

        @Test
        @DisplayName("Should initialize goals to zero")
        void shouldInitializeGoalsToZero() {
            // Assert
            assertThat(match.getHomeGoals()).isZero();
            assertThat(match.getAwayGoals()).isZero();
        }

        @Test
        @DisplayName("Should support builder pattern")
        void shouldSupportBuilderPattern() {
            // Act
            Match builtMatch = Match.builder()
                    .id(UUID.randomUUID())
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .homeGoals(2)
                    .awayGoals(1)
                    .status(MatchStatus.FINISHED)
                    .build();

            // Assert
            assertThat(builtMatch).isNotNull();
            assertThat(builtMatch.getHomeGoals()).isEqualTo(2);
            assertThat(builtMatch.getAwayGoals()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Match Goals Management")
    class MatchGoalsManagement {

        @Test
        @DisplayName("Should update home team goals")
        void shouldUpdateHomeTeamGoals() {
            // Act
            match.setHomeGoals(3);

            // Assert
            assertThat(match.getHomeGoals()).isEqualTo(3);
            assertThat(match.getAwayGoals()).isZero();
        }

        @Test
        @DisplayName("Should update away team goals")
        void shouldUpdateAwayTeamGoals() {
            // Act
            match.setAwayGoals(2);

            // Assert
            assertThat(match.getAwayGoals()).isEqualTo(2);
            assertThat(match.getHomeGoals()).isZero();
        }

        @Test
        @DisplayName("Should update both team goals independently")
        void shouldUpdateBothTeamGoalsIndependently() {
            // Act
            match.setHomeGoals(2);
            match.setAwayGoals(1);

            // Assert
            assertThat(match.getHomeGoals()).isEqualTo(2);
            assertThat(match.getAwayGoals()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle high goal scores")
        void shouldHandleHighGoalScores() {
            // Act
            match.setHomeGoals(10);
            match.setAwayGoals(9);

            // Assert
            assertThat(match.getHomeGoals()).isEqualTo(10);
            assertThat(match.getAwayGoals()).isEqualTo(9);
        }
    }

    @Nested
    @DisplayName("Match Status Management")
    class MatchStatusManagement {

        @Test
        @DisplayName("Should set match status to SCHEDULED initially")
        void shouldSetMatchStatusToScheduledInitially() {
            // Assert
            assertThat(match.getStatus()).isEqualTo(MatchStatus.SCHEDULED);
        }

        @Test
        @DisplayName("Should update match status to LIVE")
        void shouldUpdateMatchStatusToLive() {
            // Act
            match.setStatus(MatchStatus.LIVE);

            // Assert
            assertThat(match.getStatus()).isEqualTo(MatchStatus.LIVE);
        }

        @Test
        @DisplayName("Should update match status to FINISHED")
        void shouldUpdateMatchStatusToFinished() {
            // Act
            match.setStatus(MatchStatus.FINISHED);

            // Assert
            assertThat(match.getStatus()).isEqualTo(MatchStatus.FINISHED);
        }

        @Test
        @DisplayName("Should support status transitions")
        void shouldSupportStatusTransitions() {
            // Act
            match.setStatus(MatchStatus.LIVE);
            assertThat(match.getStatus()).isEqualTo(MatchStatus.LIVE);

            match.setStatus(MatchStatus.FINISHED);
            assertThat(match.getStatus()).isEqualTo(MatchStatus.FINISHED);
        }
    }

    @Nested
    @DisplayName("Match Odds Management")
    class MatchOddsManagement {

        @Test
        @DisplayName("Should set odds for match")
        void shouldSetOddsForMatch() {
            // Arrange
            Odds odds = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("1.50"))
                    .draw(new BigDecimal("3.25"))
                    .awayWin(new BigDecimal("5.00"))
                    .build();

            // Act
            match.setOdds(odds);

            // Assert
            assertThat(match.getOdds()).isEqualTo(odds);
            assertThat(odds.getMatch()).isEqualTo(match);
        }

        @Test
        @DisplayName("Should update odds association when setting new odds")
        void shouldUpdateOddsAssociationWhenSettingNewOdds() {
            // Arrange
            Odds odds1 = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("1.50"))
                    .draw(new BigDecimal("3.25"))
                    .awayWin(new BigDecimal("5.00"))
                    .build();

            Odds odds2 = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("1.80"))
                    .draw(new BigDecimal("3.00"))
                    .awayWin(new BigDecimal("4.50"))
                    .build();

            // Act
            match.setOdds(odds1);
            assertThat(match.getOdds()).isEqualTo(odds1);

            match.setOdds(odds2);
            assertThat(match.getOdds()).isEqualTo(odds2);

            // Assert
            assertThat(odds2.getMatch()).isEqualTo(match);
        }

        @Test
        @DisplayName("Should maintain bidirectional relationship with odds")
        void shouldMaintainBidirectionalRelationshipWithOdds() {
            // Arrange
            Odds odds = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("1.50"))
                    .draw(new BigDecimal("3.25"))
                    .awayWin(new BigDecimal("5.00"))
                    .build();

            // Act
            match.setOdds(odds);

            // Assert
            assertThat(match.getOdds().getMatch()).isEqualTo(match);
        }
    }

    @Nested
    @DisplayName("Match Team Relationships")
    class MatchTeamRelationships {

        @Test
        @DisplayName("Should maintain different home and away team IDs")
        void shouldMaintainDifferentHomeAndAwayTeamIds() {
            // Assert
            assertThat(match.getHomeTeamId()).isNotEqualTo(match.getAwayTeamId());
        }

        @Test
        @DisplayName("Should allow updating home team ID")
        void shouldAllowUpdatingHomeTeamId() {
            // Arrange
            UUID newHomeTeamId = UUID.randomUUID();

            // Act
            match.setHomeTeamId(newHomeTeamId);

            // Assert
            assertThat(match.getHomeTeamId()).isEqualTo(newHomeTeamId);
        }

        @Test
        @DisplayName("Should allow updating away team ID")
        void shouldAllowUpdatingAwayTeamId() {
            // Arrange
            UUID newAwayTeamId = UUID.randomUUID();

            // Act
            match.setAwayTeamId(newAwayTeamId);

            // Assert
            assertThat(match.getAwayTeamId()).isEqualTo(newAwayTeamId);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesAndSpecialScenarios {

        @Test
        @DisplayName("Should handle match with same goals")
        void shouldHandleMatchWithSameGoals() {
            // Act
            match.setHomeGoals(2);
            match.setAwayGoals(2);

            // Assert
            assertThat(match.getHomeGoals()).isEqualTo(match.getAwayGoals());
        }

        @Test
        @DisplayName("Should handle match where home team wins")
        void shouldHandleMatchWhereHomeTeamWins() {
            // Act
            match.setHomeGoals(3);
            match.setAwayGoals(1);
            match.setStatus(MatchStatus.FINISHED);

            // Assert
            assertThat(match.getHomeGoals()).isGreaterThan(match.getAwayGoals());
            assertThat(match.getStatus()).isEqualTo(MatchStatus.FINISHED);
        }

        @Test
        @DisplayName("Should handle match where away team wins")
        void shouldHandleMatchWhereAwayTeamWins() {
            // Act
            match.setHomeGoals(1);
            match.setAwayGoals(3);
            match.setStatus(MatchStatus.FINISHED);

            // Assert
            assertThat(match.getAwayGoals()).isGreaterThan(match.getHomeGoals());
        }

        @Test
        @DisplayName("Should support complete match lifecycle")
        void shouldSupportCompleteMatchLifecycle() {
            // Act & Assert
            assertThat(match.getStatus()).isEqualTo(MatchStatus.SCHEDULED);

            match.setStatus(MatchStatus.LIVE);
            assertThat(match.getStatus()).isEqualTo(MatchStatus.LIVE);

            match.setHomeGoals(2);
            match.setAwayGoals(1);

            match.setStatus(MatchStatus.FINISHED);
            assertThat(match.getStatus()).isEqualTo(MatchStatus.FINISHED);
            assertThat(match.getHomeGoals()).isEqualTo(2);
            assertThat(match.getAwayGoals()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should maintain data integrity through multiple updates")
        void shouldMaintainDataIntegrityThroughMultipleUpdates() {
            // Act
            match.setHomeGoals(1);
            UUID initialId = match.getId();
            UUID initialHomeTeamId = match.getHomeTeamId();
            UUID initialAwayTeamId = match.getAwayTeamId();

            match.setAwayGoals(2);
            match.setStatus(MatchStatus.LIVE);

            // Assert
            assertThat(match.getId()).isEqualTo(initialId);
            assertThat(match.getHomeTeamId()).isEqualTo(initialHomeTeamId);
            assertThat(match.getAwayTeamId()).isEqualTo(initialAwayTeamId);
        }
    }
}

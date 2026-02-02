package com.poorbet.matchservice.match.match.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Odds Entity Unit Tests")
class OddsTest {

    private Odds odds;
    private UUID oddsId;
    private UUID matchId;

    @BeforeEach
    void setUp() {
        oddsId = UUID.randomUUID();
        matchId = UUID.randomUUID();

        odds = Odds.builder()
                .id(oddsId)
                .homeWin(new BigDecimal("1.50"))
                .draw(new BigDecimal("3.25"))
                .awayWin(new BigDecimal("5.00"))
                .build();
    }

    @Nested
    @DisplayName("Odds Creation and Initialization")
    class OddsCreationAndInitialization {

        @Test
        @DisplayName("Should create odds with all required fields")
        void shouldCreateOddsWithAllRequiredFields() {
            // Assert
            assertThat(odds)
                    .isNotNull()
                    .satisfies(o -> {
                        assertThat(o.getId()).isEqualTo(oddsId);
                        assertThat(o.getHomeWin()).isEqualByComparingTo(new BigDecimal("1.50"));
                        assertThat(o.getDraw()).isEqualByComparingTo(new BigDecimal("3.25"));
                        assertThat(o.getAwayWin()).isEqualByComparingTo(new BigDecimal("5.00"));
                    });
        }

        @Test
        @DisplayName("Should support builder pattern")
        void shouldSupportBuilderPattern() {
            // Act
            Odds builtOdds = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("2.00"))
                    .draw(new BigDecimal("3.50"))
                    .awayWin(new BigDecimal("3.75"))
                    .build();

            // Assert
            assertThat(builtOdds).isNotNull();
            assertThat(builtOdds.getHomeWin()).isEqualByComparingTo(new BigDecimal("2.00"));
        }

        @Test
        @DisplayName("Should allow creating odds without ID")
        void shouldAllowCreatingOddsWithoutId() {
            // Act
            Odds newOdds = Odds.builder()
                    .homeWin(new BigDecimal("1.80"))
                    .draw(new BigDecimal("3.00"))
                    .awayWin(new BigDecimal("4.50"))
                    .build();

            // Assert
            assertThat(newOdds).isNotNull();
            assertThat(newOdds.getId()).isNull();
        }
    }

    @Nested
    @DisplayName("Odds Values Management")
    class OddsValuesManagement {

        @Test
        @DisplayName("Should update home win odds")
        void shouldUpdateHomeWinOdds() {
            // Act
            BigDecimal newHomeWinOdds = new BigDecimal("2.50");
            odds.setHomeWin(newHomeWinOdds);

            // Assert
            assertThat(odds.getHomeWin()).isEqualByComparingTo(newHomeWinOdds);
        }

        @Test
        @DisplayName("Should update draw odds")
        void shouldUpdateDrawOdds() {
            // Act
            BigDecimal newDrawOdds = new BigDecimal("3.75");
            odds.setDraw(newDrawOdds);

            // Assert
            assertThat(odds.getDraw()).isEqualByComparingTo(newDrawOdds);
        }

        @Test
        @DisplayName("Should update away win odds")
        void shouldUpdateAwayWinOdds() {
            // Act
            BigDecimal newAwayWinOdds = new BigDecimal("6.00");
            odds.setAwayWin(newAwayWinOdds);

            // Assert
            assertThat(odds.getAwayWin()).isEqualByComparingTo(newAwayWinOdds);
        }

        @Test
        @DisplayName("Should update all odds independently")
        void shouldUpdateAllOddsIndependently() {
            // Act
            BigDecimal newHomeWin = new BigDecimal("1.80");
            BigDecimal newDraw = new BigDecimal("3.50");
            BigDecimal newAwayWin = new BigDecimal("5.50");

            odds.setHomeWin(newHomeWin);
            odds.setDraw(newDraw);
            odds.setAwayWin(newAwayWin);

            // Assert
            assertThat(odds.getHomeWin()).isEqualByComparingTo(newHomeWin);
            assertThat(odds.getDraw()).isEqualByComparingTo(newDraw);
            assertThat(odds.getAwayWin()).isEqualByComparingTo(newAwayWin);
        }
    }

    @Nested
    @DisplayName("Odds Match Relationship")
    class OddsMatchRelationship {

        @Test
        @DisplayName("Should set match for odds")
        void shouldSetMatchForOdds() {
            // Arrange
            Match match = Match.builder()
                    .id(matchId)
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .build();

            // Act
            odds.setMatch(match);

            // Assert
            assertThat(odds.getMatch()).isEqualTo(match);
        }

        @Test
        @DisplayName("Should maintain reference to match")
        void shouldMaintainReferenceToMatch() {
            // Arrange
            Match match = Match.builder()
                    .id(matchId)
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .build();

            // Act
            odds.setMatch(match);
            Match retrievedMatch = odds.getMatch();

            // Assert
            assertThat(retrievedMatch).isEqualTo(match);
            assertThat(retrievedMatch.getId()).isEqualTo(matchId);
        }

        @Test
        @DisplayName("Should allow updating match reference")
        void shouldAllowUpdatingMatchReference() {
            // Arrange
            Match match1 = Match.builder()
                    .id(UUID.randomUUID())
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .build();

            Match match2 = Match.builder()
                    .id(UUID.randomUUID())
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .build();

            // Act
            odds.setMatch(match1);
            assertThat(odds.getMatch()).isEqualTo(match1);

            odds.setMatch(match2);
            assertThat(odds.getMatch()).isEqualTo(match2);

            // Assert
            assertThat(odds.getMatch().getId()).isNotEqualTo(match1.getId());
        }

        @Test
        @DisplayName("Should handle null match")
        void shouldHandleNullMatch() {
            // Act
            odds.setMatch(null);

            // Assert
            assertThat(odds.getMatch()).isNull();
        }
    }

    @Nested
    @DisplayName("Odds Precision and BigDecimal Handling")
    class OddsPrecisionAndBigDecimalHandling {

        @Test
        @DisplayName("Should handle high precision odds")
        void shouldHandleHighPrecisionOdds() {
            // Arrange
            BigDecimal preciseOdds = new BigDecimal("1.50123456789");

            // Act
            odds.setHomeWin(preciseOdds);

            // Assert
            assertThat(odds.getHomeWin()).isEqualByComparingTo(preciseOdds);
        }

        @Test
        @DisplayName("Should handle very low odds")
        void shouldHandleVeryLowOdds() {
            // Arrange
            BigDecimal lowOdds = new BigDecimal("1.01");

            // Act
            odds.setHomeWin(lowOdds);

            // Assert
            assertThat(odds.getHomeWin()).isEqualByComparingTo(lowOdds);
        }

        @Test
        @DisplayName("Should handle very high odds")
        void shouldHandleVeryHighOdds() {
            // Arrange
            BigDecimal highOdds = new BigDecimal("999.99");

            // Act
            odds.setAwayWin(highOdds);

            // Assert
            assertThat(odds.getAwayWin()).isEqualByComparingTo(highOdds);
        }

        @Test
        @DisplayName("Should compare odds correctly with BigDecimal")
        void shouldCompareOddsCorrectlyWithBigDecimal() {
            // Arrange
            BigDecimal odds1 = new BigDecimal("1.50");
            BigDecimal odds2 = new BigDecimal("1.50");

            // Act & Assert
            assertThat(odds1).isEqualByComparingTo(odds2);
        }

        @Test
        @DisplayName("Should handle null odds values")
        void shouldHandleNullOddsValues() {
            // Act
            odds.setHomeWin(null);
            odds.setDraw(null);
            odds.setAwayWin(null);

            // Assert
            assertThat(odds.getHomeWin()).isNull();
            assertThat(odds.getDraw()).isNull();
            assertThat(odds.getAwayWin()).isNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesAndSpecialScenarios {

        @Test
        @DisplayName("Should create odds with equal values for all types")
        void shouldCreateOddsWithEqualValuesForAllTypes() {
            // Arrange
            BigDecimal equalOdds = new BigDecimal("2.50");

            // Act
            Odds equalOdds_entity = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(equalOdds)
                    .draw(equalOdds)
                    .awayWin(equalOdds)
                    .build();

            // Assert
            assertThat(equalOdds_entity.getHomeWin()).isEqualByComparingTo(equalOdds);
            assertThat(equalOdds_entity.getDraw()).isEqualByComparingTo(equalOdds);
            assertThat(equalOdds_entity.getAwayWin()).isEqualByComparingTo(equalOdds);
        }

        @Test
        @DisplayName("Should maintain data integrity through multiple updates")
        void shouldMaintainDataIntegrityThroughMultipleUpdates() {
            // Act
            UUID initialId = odds.getId();
            odds.setHomeWin(new BigDecimal("2.00"));
            odds.setDraw(new BigDecimal("3.00"));
            odds.setAwayWin(new BigDecimal("4.00"));

            // Assert
            assertThat(odds.getId()).isEqualTo(initialId);
            assertThat(odds.getHomeWin()).isEqualByComparingTo(new BigDecimal("2.00"));
            assertThat(odds.getDraw()).isEqualByComparingTo(new BigDecimal("3.00"));
            assertThat(odds.getAwayWin()).isEqualByComparingTo(new BigDecimal("4.00"));
        }

        @Test
        @DisplayName("Should handle realistic sports betting odds")
        void shouldHandleRealisticSportsBettingOdds() {
            // Arrange
            Odds realisticOdds = Odds.builder()
                    .id(UUID.randomUUID())
                    .homeWin(new BigDecimal("1.91"))
                    .draw(new BigDecimal("3.40"))
                    .awayWin(new BigDecimal("3.97"))
                    .build();

            // Assert
            assertThat(realisticOdds.getHomeWin()).isGreaterThan(new BigDecimal("1.00"));
            assertThat(realisticOdds.getDraw()).isGreaterThan(new BigDecimal("1.00"));
            assertThat(realisticOdds.getAwayWin()).isGreaterThan(new BigDecimal("1.00"));
        }

        @Test
        @DisplayName("Should preserve odds values after setting match")
        void shouldPreserveOddsValuesAfterSettingMatch() {
            // Arrange
            BigDecimal homeWin = new BigDecimal("1.50");
            BigDecimal draw = new BigDecimal("3.25");
            BigDecimal awayWin = new BigDecimal("5.00");

            Match match = Match.builder()
                    .id(UUID.randomUUID())
                    .homeTeamId(UUID.randomUUID())
                    .awayTeamId(UUID.randomUUID())
                    .build();

            odds.setHomeWin(homeWin);
            odds.setDraw(draw);
            odds.setAwayWin(awayWin);

            // Act
            odds.setMatch(match);

            // Assert
            assertThat(odds.getHomeWin()).isEqualByComparingTo(homeWin);
            assertThat(odds.getDraw()).isEqualByComparingTo(draw);
            assertThat(odds.getAwayWin()).isEqualByComparingTo(awayWin);
            assertThat(odds.getMatch()).isEqualTo(match);
        }
    }
}

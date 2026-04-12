package com.poorbet.couponservice.domain;

import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BetType Enum Unit Tests")
class BetTypeTest {

    @Test
    @DisplayName("Should map HOME_WIN to WON when home has more goals")
    void shouldMapHomeWinToWonWhenHomeHasMoreGoals() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                2,
                1);

        // Act
        BetStatus status = BetType.HOME_WIN.mapToStatus(result, 2, 1);

        // Assert
        assertThat(status).isEqualTo(BetStatus.WON);
    }

    @Test
    @DisplayName("Should map HOME_WIN to LOST when home has fewer goals")
    void shouldMapHomeWinToLostWhenHomeHasFewerGoals() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                1,
                2);


        // Act
        BetStatus status = BetType.HOME_WIN.mapToStatus(result, 1, 2);

        // Assert
        assertThat(status).isEqualTo(BetStatus.LOST);
    }

    @Test
    @DisplayName("Should map HOME_WIN to LOST when match is draw")
    void shouldMapHomeWinToLostWhenMatchIsDraw() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                1,
                1);


        // Act
        BetStatus status = BetType.HOME_WIN.mapToStatus(result, 1, 1);

        // Assert
        assertThat(status).isEqualTo(BetStatus.LOST);
    }

    @Test
    @DisplayName("Should map DRAW to WON when match is draw")
    void shouldMapDrawToWonWhenMatchIsDraw() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                2,
                2);


        // Act
        BetStatus status = BetType.DRAW.mapToStatus(result, 2, 2);

        // Assert
        assertThat(status).isEqualTo(BetStatus.WON);
    }

    @Test
    @DisplayName("Should map DRAW to LOST when home wins")
    void shouldMapDrawToLostWhenHomeWins() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                3,
                1);


        // Act
        BetStatus status = BetType.DRAW.mapToStatus(result, 3, 1);

        // Assert
        assertThat(status).isEqualTo(BetStatus.LOST);
    }

    @Test
    @DisplayName("Should map DRAW to LOST when away wins")
    void shouldMapDrawToLostWhenAwayWins() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                0,
                2);


        // Act
        BetStatus status = BetType.DRAW.mapToStatus(result, 0, 2);

        // Assert
        assertThat(status).isEqualTo(BetStatus.LOST);
    }

    @Test
    @DisplayName("Should map AWAY_WIN to WON when away has more goals")
    void shouldMapAwayWinToWonWhenAwayHasMoreGoals() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                1,
                3);


        // Act
        BetStatus status = BetType.AWAY_WIN.mapToStatus(result, 1, 3);

        // Assert
        assertThat(status).isEqualTo(BetStatus.WON);
    }

    @Test
    @DisplayName("Should map AWAY_WIN to LOST when away has fewer goals")
    void shouldMapAwayWinToLostWhenAwayHasFewerGoals() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                2,
                0);


        // Act
        BetStatus status = BetType.AWAY_WIN.mapToStatus(result, 2, 0);

        // Assert
        assertThat(status).isEqualTo(BetStatus.LOST);
    }

    @Test
    @DisplayName("Should map AWAY_WIN to LOST when match is draw")
    void shouldMapAwayWinToLostWhenMatchIsDraw() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                1,
                1);


        // Act
        BetStatus status = BetType.AWAY_WIN.mapToStatus(result, 1, 1);

        // Assert
        assertThat(status).isEqualTo(BetStatus.LOST);
    }

    @Test
    @DisplayName("Should return PENDING when result is null")
    void shouldReturnPendingWhenResultIsNull() {
        // Act
        BetStatus status = BetType.HOME_WIN.mapToStatus(null, 0, 0);

        // Assert
        assertThat(status).isEqualTo(BetStatus.PENDING);
    }

    @ParameterizedTest
    @CsvSource({
            "5, 0, WON",
            "3, 1, WON",
            "2, 1, WON",
            "1, 0, WON",
            "0, 0, LOST",
            "1, 1, LOST",
            "0, 1, LOST",
            "1, 5, LOST"
    })
    @DisplayName("Should correctly map HOME_WIN status with various scores")
    void shouldCorrectlyMapHomeWinStatusWithVariousScores(int homeGoals, int awayGoals, String expectedStatus) {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                homeGoals,
                awayGoals);


        // Act
        BetStatus status = BetType.HOME_WIN.mapToStatus(result, homeGoals, awayGoals);

        // Assert
        assertThat(status).isEqualTo(BetStatus.valueOf(expectedStatus));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, WON",
            "1, 1, WON",
            "2, 2, WON",
            "5, 5, WON",
            "0, 1, LOST",
            "1, 0, LOST",
            "3, 2, LOST",
            "2, 3, LOST"
    })
    @DisplayName("Should correctly map DRAW status with various scores")
    void shouldCorrectlyMapDrawStatusWithVariousScores(int homeGoals, int awayGoals, String expectedStatus) {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                homeGoals,
                awayGoals);

        // Act
        BetStatus status = BetType.DRAW.mapToStatus(result, homeGoals, awayGoals);

        // Assert
        assertThat(status).isEqualTo(BetStatus.valueOf(expectedStatus));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5, WON",
            "1, 3, WON",
            "1, 2, WON",
            "0, 1, WON",
            "0, 0, LOST",
            "1, 1, LOST",
            "1, 0, LOST",
            "5, 1, LOST"
    })
    @DisplayName("Should correctly map AWAY_WIN status with various scores")
    void shouldCorrectlyMapAwayWinStatusWithVariousScores(int homeGoals, int awayGoals, String expectedStatus) {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                homeGoals,
                awayGoals);

        // Act
        BetStatus status = BetType.AWAY_WIN.mapToStatus(result, homeGoals, awayGoals);

        // Assert
        assertThat(status).isEqualTo(BetStatus.valueOf(expectedStatus));
    }

    @Test
    @DisplayName("Should handle edge case: 0-0 draw for all bet types")
    void shouldHandleEdgeCaseZeroZeroDraw() {
        // Arrange
        MatchResultEventDto result = new MatchResultEventDto(
                java.util.UUID.randomUUID(),
                0,
                0);

        // Act & Assert
        assertThat(BetType.HOME_WIN.mapToStatus(result, 0, 0)).isEqualTo(BetStatus.LOST);
        assertThat(BetType.DRAW.mapToStatus(result, 0, 0)).isEqualTo(BetStatus.WON);
        assertThat(BetType.AWAY_WIN.mapToStatus(result, 0, 0)).isEqualTo(BetStatus.LOST);
    }
}

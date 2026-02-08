package com.poorbet.matchservice.match.match.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.projections.AwayWin;
import com.poorbet.matchservice.match.match.projections.Draw;
import com.poorbet.matchservice.match.match.projections.HomeWin;
import com.poorbet.matchservice.match.match.repository.OddsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OddsService Implementation Unit Tests")
class OddsServiceImplTest {

    @Mock
    private OddsRepository oddsRepository;

    @InjectMocks
    private OddsServiceImpl oddsService;

    private UUID testMatchId;
    private static final BigDecimal TEST_HOME_WIN_ODDS = new BigDecimal("1.50");
    private static final BigDecimal TEST_DRAW_ODDS = new BigDecimal("3.25");
    private static final BigDecimal TEST_AWAY_WIN_ODDS = new BigDecimal("5.00");

    @BeforeEach
    void setUp() {
        testMatchId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Get Home Win Odds")
    class GetHomeWinOdds {

        @Test
        @DisplayName("Should return home win odds when available")
        void shouldReturnHomeWinOddsWhenAvailable() {
            // Arrange
            HomeWin homeWinProjection = () -> TEST_HOME_WIN_ODDS;
            when(oddsRepository.findHomeWinByMatchId(testMatchId))
                    .thenReturn(Optional.of(homeWinProjection));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result)
                    .isPresent()
                    .hasValue(TEST_HOME_WIN_ODDS);
            verify(oddsRepository, times(1)).findHomeWinByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should return empty optional when home win odds not found")
        void shouldReturnEmptyOptionalWhenHomeWinOddsNotFound() {
            // Arrange
            when(oddsRepository.findHomeWinByMatchId(testMatchId))
                    .thenReturn(Optional.empty());

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result).isEmpty();
            verify(oddsRepository).findHomeWinByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should call repository with correct match ID")
        void shouldCallRepositoryWithCorrectMatchId() {
            // Arrange
            UUID otherMatchId = UUID.randomUUID();
            when(oddsRepository.findHomeWinByMatchId(any())).thenReturn(Optional.empty());

            // Act
            oddsService.getOdds(otherMatchId, OddsType.HOME_WIN);

            // Assert
            verify(oddsRepository).findHomeWinByMatchId(otherMatchId);
        }
    }

    @Nested
    @DisplayName("Get Draw Odds")
    class GetDrawOdds {

        @Test
        @DisplayName("Should return draw odds when available")
        void shouldReturnDrawOddsWhenAvailable() {
            // Arrange
            Draw drawProjection = () -> TEST_DRAW_ODDS;
            when(oddsRepository.findDrawByMatchId(testMatchId))
                    .thenReturn(Optional.of(drawProjection));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.DRAW);

            // Assert
            assertThat(result)
                    .isPresent()
                    .hasValue(TEST_DRAW_ODDS);
            verify(oddsRepository, times(1)).findDrawByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should return empty optional when draw odds not found")
        void shouldReturnEmptyOptionalWhenDrawOddsNotFound() {
            // Arrange
            when(oddsRepository.findDrawByMatchId(testMatchId))
                    .thenReturn(Optional.empty());

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.DRAW);

            // Assert
            assertThat(result).isEmpty();
            verify(oddsRepository).findDrawByMatchId(testMatchId);
        }
    }

    @Nested
    @DisplayName("Get Away Win Odds")
    class GetAwayWinOdds {

        @Test
        @DisplayName("Should return away win odds when available")
        void shouldReturnAwayWinOddsWhenAvailable() {
            // Arrange
            AwayWin awayWinProjection = () -> TEST_AWAY_WIN_ODDS;
            when(oddsRepository.findAwayWinByMatchId(testMatchId))
                    .thenReturn(Optional.of(awayWinProjection));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.AWAY_WIN);

            // Assert
            assertThat(result)
                    .isPresent()
                    .hasValue(TEST_AWAY_WIN_ODDS);
            verify(oddsRepository, times(1)).findAwayWinByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should return empty optional when away win odds not found")
        void shouldReturnEmptyOptionalWhenAwayWinOddsNotFound() {
            // Arrange
            when(oddsRepository.findAwayWinByMatchId(testMatchId))
                    .thenReturn(Optional.empty());

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.AWAY_WIN);

            // Assert
            assertThat(result).isEmpty();
            verify(oddsRepository).findAwayWinByMatchId(testMatchId);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Multiple Calls")
    class EdgeCases {

        @Test
        @DisplayName("Should handle multiple consecutive calls correctly")
        void shouldHandleMultipleConsecutiveCallsCorrectly() {
            // Arrange
            HomeWin homeWinProjection = () -> TEST_HOME_WIN_ODDS;
            when(oddsRepository.findHomeWinByMatchId(testMatchId))
                    .thenReturn(Optional.of(homeWinProjection));

            // Act
            Optional<BigDecimal> result1 = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);
            Optional<BigDecimal> result2 = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result1).isEqualTo(result2);
            verify(oddsRepository, times(2)).findHomeWinByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should handle null match ID gracefully")
        void shouldHandleNullMatchIdGracefully() {
            // Arrange
            when(oddsRepository.findHomeWinByMatchId(null))
                    .thenThrow(NullPointerException.class);

            // Act & Assert
            assertThatThrownBy(() -> oddsService.getOdds(null, OddsType.HOME_WIN))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle different match IDs independently")
        void shouldHandleDifferentMatchIdsIndependently() {
            // Arrange
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();
            HomeWin homeWinProjection1 = () -> TEST_HOME_WIN_ODDS;
            HomeWin homeWinProjection2 = () -> new BigDecimal("2.00");

            when(oddsRepository.findHomeWinByMatchId(matchId1))
                    .thenReturn(Optional.of(homeWinProjection1));
            when(oddsRepository.findHomeWinByMatchId(matchId2))
                    .thenReturn(Optional.of(homeWinProjection2));

            // Act
            Optional<BigDecimal> result1 = oddsService.getOdds(matchId1, OddsType.HOME_WIN);
            Optional<BigDecimal> result2 = oddsService.getOdds(matchId2, OddsType.HOME_WIN);

            // Assert
            assertThat(result1).hasValue(TEST_HOME_WIN_ODDS);
            assertThat(result2).hasValue(new BigDecimal("2.00"));
        }

        @Test
        @DisplayName("Should handle very high odds values")
        void shouldHandleVeryHighOddsValues() {
            // Arrange
            BigDecimal highOdds = new BigDecimal("999.99");
            HomeWin homeWinProjection = () -> highOdds;
            when(oddsRepository.findHomeWinByMatchId(testMatchId))
                    .thenReturn(Optional.of(homeWinProjection));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result).hasValue(highOdds);
        }

        @Test
        @DisplayName("Should handle minimum odds values")
        void shouldHandleMinimumOddsValues() {
            // Arrange
            BigDecimal minOdds = new BigDecimal("1.00");
            HomeWin homeWinProjection = () -> minOdds;
            when(oddsRepository.findHomeWinByMatchId(testMatchId))
                    .thenReturn(Optional.of(homeWinProjection));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result).hasValue(minOdds);
        }
    }
}

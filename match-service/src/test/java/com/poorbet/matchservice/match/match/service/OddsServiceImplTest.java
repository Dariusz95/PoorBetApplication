package com.poorbet.matchservice.match.match.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.poorbet.matchservice.match.match.domain.Odds;
import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.repository.OddsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
    private static final BigDecimal TEST_OVER_2_5_ODDS = new BigDecimal("1.80");
    private static final BigDecimal TEST_UNDER_2_5_ODDS = new BigDecimal("2.10");
    private static final BigDecimal TEST_OVER_3_5_ODDS = new BigDecimal("3.40");
    private static final BigDecimal TEST_UNDER_3_5_ODDS = new BigDecimal("1.30");

    @BeforeEach
    void setUp() {
        testMatchId = UUID.randomUUID();
    }

    private static Odds fullOdds() {
        return Odds.builder()
                .homeWin(TEST_HOME_WIN_ODDS)
                .draw(TEST_DRAW_ODDS)
                .awayWin(TEST_AWAY_WIN_ODDS)
                .over25(TEST_OVER_2_5_ODDS)
                .under25(TEST_UNDER_2_5_ODDS)
                .over35(TEST_OVER_3_5_ODDS)
                .under35(TEST_UNDER_3_5_ODDS)
                .build();
    }

    @Nested
    @DisplayName("Get Odds By Type")
    class GetOddsByType {

        @ParameterizedTest(name = "{0}")
        @EnumSource(OddsType.class)
        @DisplayName("Should return the odds value matching the requested type")
        void shouldReturnOddsValueForType(OddsType type) {
            // Arrange
            when(oddsRepository.findByMatchId(testMatchId)).thenReturn(Optional.of(fullOdds()));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, type);

            // Assert
            BigDecimal expected = switch (type) {
                case HOME_WIN -> TEST_HOME_WIN_ODDS;
                case DRAW -> TEST_DRAW_ODDS;
                case AWAY_WIN -> TEST_AWAY_WIN_ODDS;
                case OVER_2_5 -> TEST_OVER_2_5_ODDS;
                case UNDER_2_5 -> TEST_UNDER_2_5_ODDS;
                case OVER_3_5 -> TEST_OVER_3_5_ODDS;
                case UNDER_3_5 -> TEST_UNDER_3_5_ODDS;
            };
            assertThat(result).isPresent().hasValue(expected);
        }

        @Test
        @DisplayName("Should return empty optional when odds not found for match")
        void shouldReturnEmptyOptionalWhenOddsNotFound() {
            // Arrange
            when(oddsRepository.findByMatchId(testMatchId)).thenReturn(Optional.empty());

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result).isEmpty();
            verify(oddsRepository).findByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should call repository with correct match ID")
        void shouldCallRepositoryWithCorrectMatchId() {
            // Arrange
            UUID otherMatchId = UUID.randomUUID();
            when(oddsRepository.findByMatchId(any())).thenReturn(Optional.empty());

            // Act
            oddsService.getOdds(otherMatchId, OddsType.HOME_WIN);

            // Assert
            verify(oddsRepository).findByMatchId(otherMatchId);
        }

        @Test
        @DisplayName("Should only query the repository once per call, regardless of requested type")
        void shouldOnlyQueryRepositoryOnce() {
            // Arrange
            when(oddsRepository.findByMatchId(testMatchId)).thenReturn(Optional.of(fullOdds()));

            // Act
            oddsService.getOdds(testMatchId, OddsType.OVER_3_5);

            // Assert
            verify(oddsRepository, times(1)).findByMatchId(testMatchId);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Multiple Calls")
    class EdgeCases {

        @Test
        @DisplayName("Should handle multiple consecutive calls correctly")
        void shouldHandleMultipleConsecutiveCallsCorrectly() {
            // Arrange
            when(oddsRepository.findByMatchId(testMatchId)).thenReturn(Optional.of(fullOdds()));

            // Act
            Optional<BigDecimal> result1 = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);
            Optional<BigDecimal> result2 = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result1).isEqualTo(result2);
            verify(oddsRepository, times(2)).findByMatchId(testMatchId);
        }

        @Test
        @DisplayName("Should handle null match ID gracefully")
        void shouldHandleNullMatchIdGracefully() {
            // Arrange
            when(oddsRepository.findByMatchId(null)).thenThrow(NullPointerException.class);

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
            Odds odds1 = Odds.builder().homeWin(TEST_HOME_WIN_ODDS).build();
            Odds odds2 = Odds.builder().homeWin(new BigDecimal("2.00")).build();

            when(oddsRepository.findByMatchId(matchId1)).thenReturn(Optional.of(odds1));
            when(oddsRepository.findByMatchId(matchId2)).thenReturn(Optional.of(odds2));

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
            when(oddsRepository.findByMatchId(testMatchId))
                    .thenReturn(Optional.of(Odds.builder().homeWin(highOdds).build()));

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
            when(oddsRepository.findByMatchId(testMatchId))
                    .thenReturn(Optional.of(Odds.builder().homeWin(minOdds).build()));

            // Act
            Optional<BigDecimal> result = oddsService.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(result).hasValue(minOdds);
        }
    }
}

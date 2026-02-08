package com.poorbet.matchservice.match.match.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.match.service.MatchResultsService;
import com.poorbet.matchservice.match.match.service.OddsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchController Unit Tests")
class MatchControllerTest {

    @Mock
    private MatchResultsService matchResultsService;

    @Mock
    private OddsService oddsService;

    @InjectMocks
    private MatchController matchController;

    private UUID testMatchId;
    private List<UUID> testMatchIds;
    private MatchResultMapDto testResultMap;

    @BeforeEach
    void setUp() {
        testMatchId = UUID.randomUUID();
        testMatchIds = List.of(testMatchId, UUID.randomUUID(), UUID.randomUUID());
        testResultMap = MatchResultMapDto.builder().build();
    }

    @Nested
    @DisplayName("Get Results Endpoint")
    class GetResultsEndpoint {

        @Test
        @DisplayName("Should return match results with 200 status")
        void shouldReturnMatchResultsWith200Status() {
            // Arrange
            when(matchResultsService.getMatchResultMap(testMatchIds))
                    .thenReturn(testResultMap);

            // Act
            ResponseEntity<MatchResultMapDto> response = matchController.getResults(testMatchIds);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(testResultMap);
        }

        @Test
        @DisplayName("Should call service with provided match IDs")
        void shouldCallServiceWithProvidedMatchIds() {
            // Arrange
            when(matchResultsService.getMatchResultMap(testMatchIds))
                    .thenReturn(testResultMap);

            // Act
            matchController.getResults(testMatchIds);

            // Assert
            verify(matchResultsService, times(1)).getMatchResultMap(testMatchIds);
        }

        @Test
        @DisplayName("Should return response body from service")
        void shouldReturnResponseBodyFromService() {
            // Arrange
            MatchResultMapDto expectedResult = MatchResultMapDto.builder()
                    .results(java.util.Map.of(
                            testMatchId, MatchResultDto.builder()
                                    .id(testMatchId)
                                    .homeGoals(2)
                                    .awayGoals(1)
                                    .build()
                    ))
                    .build();
            when(matchResultsService.getMatchResultMap(testMatchIds))
                    .thenReturn(expectedResult);

            // Act
            ResponseEntity<MatchResultMapDto> response = matchController.getResults(testMatchIds);

            // Assert
            assertThat(response.getBody()).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("Should handle service exception")
        void shouldHandleServiceException() {
            // Arrange
            when(matchResultsService.getMatchResultMap(any()))
                    .thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            assertThatThrownBy(() -> matchController.getResults(testMatchIds))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Service error");
        }
    }

    @Nested
    @DisplayName("Get Odds Endpoint")
    class GetOddsEndpoint {

        @Test
        @DisplayName("Should return odds with 200 status when found")
        void shouldReturnOddsWith200StatusWhenFound() {
            // Arrange
            BigDecimal testOdds = new BigDecimal("1.50");
            when(oddsService.getOdds(testMatchId, OddsType.HOME_WIN))
                    .thenReturn(Optional.of(testOdds));

            // Act
            ResponseEntity<BigDecimal> response = matchController.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualByComparingTo(testOdds);
        }

        @Test
        @DisplayName("Should return 404 when odds not found")
        void shouldReturn404WhenOddsNotFound() {
            // Arrange
            when(oddsService.getOdds(testMatchId, OddsType.HOME_WIN))
                    .thenReturn(Optional.empty());

            // Act
            ResponseEntity<BigDecimal> response = matchController.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Should call service with correct parameters")
        void shouldCallServiceWithCorrectParameters() {
            // Arrange
            when(oddsService.getOdds(testMatchId, OddsType.DRAW))
                    .thenReturn(Optional.empty());

            // Act
            matchController.getOdds(testMatchId, OddsType.DRAW);

            // Assert
            verify(oddsService).getOdds(testMatchId, OddsType.DRAW);
        }

        @Test
        @DisplayName("Should return correct odds value")
        void shouldReturnCorrectOddsValue() {
            // Arrange
            BigDecimal expectedOdds = new BigDecimal("2.75");
            when(oddsService.getOdds(testMatchId, OddsType.HOME_WIN))
                    .thenReturn(Optional.of(expectedOdds));

            // Act
            ResponseEntity<BigDecimal> response = matchController.getOdds(testMatchId, OddsType.HOME_WIN);

            // Assert
            assertThat(response.getBody()).isEqualByComparingTo(expectedOdds);
        }

        @Test
        @DisplayName("Should handle service exception gracefully")
        void shouldHandleServiceExceptionGracefully() {
            // Arrange
            when(oddsService.getOdds(any(), any()))
                    .thenThrow(new RuntimeException("Odds service error"));

            // Act & Assert
            assertThatThrownBy(() -> matchController.getOdds(testMatchId, OddsType.HOME_WIN))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Odds service error");
        }

    }
}

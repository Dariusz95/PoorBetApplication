package com.poorbet.couponservice.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.dto.MatchResultMapDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("BetService Unit Tests")
class BetServiceTest {

    @Mock
    private MatchClient matchClient;

    @InjectMocks
    private BetService betService;

    private List<UUID> matchIds;

    @BeforeEach
    void setUp() {
        matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    @DisplayName("Should get match results from MatchClient")
    void shouldGetMatchResultsFromMatchClient() {
        // Arrange
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        when(matchClient.getMatchResult(matchIds)).thenReturn(expectedResults);

        // Act
        MatchResultMapDto result = betService.getResults(matchIds);

        // Assert
        assertThat(result).isEqualTo(expectedResults);
    }

    @Test
    @DisplayName("Should call MatchClient with provided match IDs")
    void shouldCallMatchClientWithProvidedMatchIds() {
        // Arrange
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        when(matchClient.getMatchResult(matchIds)).thenReturn(expectedResults);

        // Act
        betService.getResults(matchIds);

        // Assert
        verify(matchClient, times(1)).getMatchResult(matchIds);
    }

    @Test
    @DisplayName("Should handle single match ID")
    void shouldHandleSingleMatchId() {
        // Arrange
        List<UUID> singleMatch = Collections.singletonList(UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        when(matchClient.getMatchResult(singleMatch)).thenReturn(expectedResults);

        // Act
        MatchResultMapDto result = betService.getResults(singleMatch);

        // Assert
        assertThat(result).isNotNull();
        verify(matchClient).getMatchResult(singleMatch);
    }

    @Test
    @DisplayName("Should handle empty match ID list")
    void shouldHandleEmptyMatchIdList() {
        // Arrange
        List<UUID> emptyList = Collections.emptyList();
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        when(matchClient.getMatchResult(emptyList)).thenReturn(expectedResults);

        // Act
        MatchResultMapDto result = betService.getResults(emptyList);

        // Assert
        assertThat(result).isNotNull();
        verify(matchClient).getMatchResult(emptyList);
    }

    @Test
    @DisplayName("Should propagate MatchClient exceptions")
    void shouldPropagateMatchClientExceptions() {
        // Arrange
        when(matchClient.getMatchResult(any()))
                .thenThrow(new RuntimeException("MatchClient unavailable"));

        // Act & Assert
        assertThatThrownBy(() -> betService.getResults(matchIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("MatchClient unavailable");
    }

    @Test
    @DisplayName("Should return MatchResultMapDto from MatchClient response")
    void shouldReturnMatchResultMapDtoFromMatchClientResponse() {
        // Arrange
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        when(matchClient.getMatchResult(any())).thenReturn(expectedResults);

        // Act
        MatchResultMapDto result = betService.getResults(matchIds);

        // Assert
        assertThat(result).isSameAs(expectedResults);
    }

    @Test
    @DisplayName("Should handle large list of match IDs")
    void shouldHandleLargeListOfMatchIds() {
        // Arrange
        List<UUID> largeList = Arrays.asList(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        when(matchClient.getMatchResult(largeList)).thenReturn(expectedResults);

        // Act
        MatchResultMapDto result = betService.getResults(largeList);

        // Assert
        assertThat(result).isNotNull();
        verify(matchClient).getMatchResult(largeList);
    }
}

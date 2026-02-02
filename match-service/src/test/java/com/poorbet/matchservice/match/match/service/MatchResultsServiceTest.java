package com.poorbet.matchservice.match.match.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.match.mapper.MatchResultMapMapper;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("MatchResultsService Unit Tests")
class MatchResultsServiceTest {

    @Mock
    private MatchResultMapMapper matchResultMapMapper;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchResultsService matchResultsService;

    private List<UUID> testMatchIds;
    private List<MatchResultDto> testResults;
    private MatchResultMapDto expectedMappedResult;

    @BeforeEach
    void setUp() {
        testMatchIds = new ArrayList<>();
        testResults = new ArrayList<>();
        expectedMappedResult = MatchResultMapDto.builder().build();
    }

    @Nested
    @DisplayName("Get Match Result Map - Single Match")
    class GetMatchResultMapSingleMatch {

        @BeforeEach
        void setUp() {
            UUID matchId = UUID.randomUUID();
            testMatchIds.add(matchId);

            MatchResultDto resultDto = MatchResultDto.builder()
                    .id(matchId)
                    .homeGoals(2)
                    .awayGoals(1)
                    .build();
            testResults.add(resultDto);

            expectedMappedResult = MatchResultMapDto.builder()
                    .results(java.util.Map.of(matchId, resultDto))
                    .build();
        }

        @Test
        @DisplayName("Should retrieve and map single match result")
        void shouldRetrieveAndMapSingleMatchResult() {
            // Arrange
            when(matchRepository.findResultsByIds(testMatchIds))
                    .thenReturn(testResults);
            when(matchResultMapMapper.toDto(testResults))
                    .thenReturn(expectedMappedResult);

            // Act
            MatchResultMapDto result = matchResultsService.getMatchResultMap(testMatchIds);

            // Assert
            assertThat(result).isNotNull();
            verify(matchRepository, times(1)).findResultsByIds(testMatchIds);
            verify(matchResultMapMapper, times(1)).toDto(testResults);
        }

        @Test
        @DisplayName("Should call repository with correct match IDs")
        void shouldCallRepositoryWithCorrectMatchIds() {
            // Arrange
            when(matchRepository.findResultsByIds(testMatchIds))
                    .thenReturn(testResults);
            when(matchResultMapMapper.toDto(any()))
                    .thenReturn(expectedMappedResult);

            // Act
            matchResultsService.getMatchResultMap(testMatchIds);

            // Assert
            verify(matchRepository).findResultsByIds(testMatchIds);
        }
    }

    @Nested
    @DisplayName("Get Match Result Map - Multiple Matches")
    class GetMatchResultMapMultipleMatches {

        @BeforeEach
        void setUp() {
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();
            UUID matchId3 = UUID.randomUUID();
            testMatchIds = List.of(matchId1, matchId2, matchId3);

            testResults = List.of(
                    MatchResultDto.builder().id(matchId1).homeGoals(2).awayGoals(1).build(),
                    MatchResultDto.builder().id(matchId2).homeGoals(0).awayGoals(0).build(),
                    MatchResultDto.builder().id(matchId3).homeGoals(3).awayGoals(2).build()
            );

            expectedMappedResult = MatchResultMapDto.builder().build();
        }

        @Test
        @DisplayName("Should retrieve all match results")
        void shouldRetrieveAllMatchResults() {
            // Arrange
            when(matchRepository.findResultsByIds(testMatchIds))
                    .thenReturn(testResults);
            when(matchResultMapMapper.toDto(testResults))
                    .thenReturn(expectedMappedResult);

            // Act
            MatchResultMapDto result = matchResultsService.getMatchResultMap(testMatchIds);

            // Assert
            assertThat(result).isNotNull();
            verify(matchRepository).findResultsByIds(testMatchIds);
        }

        @Test
        @DisplayName("Should pass all results to mapper")
        void shouldPassAllResultsToMapper() {
            // Arrange
            when(matchRepository.findResultsByIds(testMatchIds))
                    .thenReturn(testResults);
            when(matchResultMapMapper.toDto(testResults))
                    .thenReturn(expectedMappedResult);

            // Act
            matchResultsService.getMatchResultMap(testMatchIds);

            // Assert
            verify(matchResultMapMapper).toDto(testResults);
        }

        @Test
        @DisplayName("Should handle matches with different goal counts")
        void shouldHandleMatchesWithDifferentGoalCounts() {
            // Arrange
            when(matchRepository.findResultsByIds(testMatchIds))
                    .thenReturn(testResults);
            when(matchResultMapMapper.toDto(testResults))
                    .thenReturn(expectedMappedResult);

            // Act
            matchResultsService.getMatchResultMap(testMatchIds);

            // Assert
            verify(matchRepository).findResultsByIds(testMatchIds);
            assertThat(testResults)
                    .hasSize(3)
                    .allSatisfy(result -> assertThat(result.getId()).isNotNull());
        }
    }

    @Nested
    @DisplayName("Get Match Result Map - Empty Results")
    class GetMatchResultMapEmptyResults {

        @Test
        @DisplayName("Should handle empty match ID list")
        void shouldHandleEmptyMatchIdList() {
            // Arrange
            List<UUID> emptyIds = Collections.emptyList();
            when(matchRepository.findResultsByIds(emptyIds))
                    .thenReturn(Collections.emptyList());
            when(matchResultMapMapper.toDto(Collections.emptyList()))
                    .thenReturn(MatchResultMapDto.builder().build());

            // Act
            MatchResultMapDto result = matchResultsService.getMatchResultMap(emptyIds);

            // Assert
            assertThat(result).isNotNull();
            verify(matchRepository).findResultsByIds(emptyIds);
        }

        @Test
        @DisplayName("Should return mapped empty result when no matches found")
        void shouldReturnMappedEmptyResultWhenNoMatchesFound() {
            // Arrange
            List<UUID> matchIds = List.of(UUID.randomUUID());
            when(matchRepository.findResultsByIds(matchIds))
                    .thenReturn(Collections.emptyList());
            when(matchResultMapMapper.toDto(Collections.emptyList()))
                    .thenReturn(MatchResultMapDto.builder().build());

            // Act
            MatchResultMapDto result = matchResultsService.getMatchResultMap(matchIds);

            // Assert
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Match Result Map - Edge Cases")
    class GetMatchResultMapEdgeCases {

        @Test
        @DisplayName("Should handle null match ID list gracefully")
        void shouldHandleNullMatchIdListGracefully() {
            // Arrange
            when(matchRepository.findResultsByIds(null))
                    .thenThrow(NullPointerException.class);

            // Act & Assert
            assertThatThrownBy(() -> matchResultsService.getMatchResultMap(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle repository exception")
        void shouldHandleRepositoryException() {
            // Arrange
            List<UUID> matchIds = List.of(UUID.randomUUID());
            when(matchRepository.findResultsByIds(matchIds))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> matchResultsService.getMatchResultMap(matchIds))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");
        }

        @Test
        @DisplayName("Should handle mapper exception")
        void shouldHandleMapperException() {
            // Arrange
            List<UUID> matchIds = List.of(UUID.randomUUID());
            when(matchRepository.findResultsByIds(matchIds))
                    .thenReturn(testResults);
            when(matchResultMapMapper.toDto(testResults))
                    .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThatThrownBy(() -> matchResultsService.getMatchResultMap(matchIds))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Mapping error");
        }

        @Test
        @DisplayName("Should handle large number of match IDs")
        void shouldHandleLargeNumberOfMatchIds() {
            // Arrange
            List<UUID> largeIdList = new ArrayList<>();
            List<MatchResultDto> largeResults = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                UUID id = UUID.randomUUID();
                largeIdList.add(id);
                largeResults.add(MatchResultDto.builder()
                        .id(id)
                        .homeGoals(i % 5)
                        .awayGoals(i % 4)
                        .build());
            }

            when(matchRepository.findResultsByIds(largeIdList))
                    .thenReturn(largeResults);
            when(matchResultMapMapper.toDto(largeResults))
                    .thenReturn(MatchResultMapDto.builder().build());

            // Act
            MatchResultMapDto result = matchResultsService.getMatchResultMap(largeIdList);

            // Assert
            assertThat(result).isNotNull();
            verify(matchRepository).findResultsByIds(largeIdList);
        }

        @Test
        @DisplayName("Should handle duplicate match IDs in request")
        void shouldHandleDuplicateMatchIdsInRequest() {
            // Arrange
            UUID matchId = UUID.randomUUID();
            List<UUID> idsWithDuplicates = List.of(matchId, matchId, matchId);
            List<MatchResultDto> uniqueResults = List.of(
                    MatchResultDto.builder().id(matchId).homeGoals(1).awayGoals(0).build()
            );

            when(matchRepository.findResultsByIds(idsWithDuplicates))
                    .thenReturn(uniqueResults);
            when(matchResultMapMapper.toDto(uniqueResults))
                    .thenReturn(MatchResultMapDto.builder().build());

            // Act
            matchResultsService.getMatchResultMap(idsWithDuplicates);

            // Assert
            verify(matchRepository).findResultsByIds(idsWithDuplicates);
        }
    }
}

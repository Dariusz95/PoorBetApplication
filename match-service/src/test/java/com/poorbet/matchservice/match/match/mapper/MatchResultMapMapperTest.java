package com.poorbet.matchservice.match.match.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.dto.MatchResultMapDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MatchResultMapMapper Unit Tests")
class MatchResultMapMapperTest {

    private MatchResultMapMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MatchResultMapMapper();
    }

    @Nested
    @DisplayName("Mapping Single Result")
    class MappingSingleResult {

        @Test
        @DisplayName("Should map single match result to DTO")
        void shouldMapSingleMatchResultToDto() {
            // Arrange
            UUID matchId = UUID.randomUUID();
            MatchResultDto resultDto = MatchResultDto.builder()
                    .id(matchId)
                    .homeGoals(2)
                    .awayGoals(1)
                    .build();
            List<MatchResultDto> results = List.of(resultDto);

            // Act
            MatchResultMapDto mappedResult = mapper.toDto(results);

            // Assert
            assertThat(mappedResult)
                    .isNotNull()
                    .satisfies(dto -> {
                        assertThat(dto.getResults()).hasSize(1);
                        assertThat(dto.getResults()).containsKey(matchId);
                        assertThat(dto.getResults().get(matchId)).isEqualTo(resultDto);
                    });
        }

        @Test
        @DisplayName("Should preserve match ID as key")
        void shouldPreserveMatchIdAsKey() {
            // Arrange
            UUID matchId = UUID.randomUUID();
            MatchResultDto resultDto = MatchResultDto.builder()
                    .id(matchId)
                    .homeGoals(3)
                    .awayGoals(0)
                    .build();

            // Act
            MatchResultMapDto result = mapper.toDto(List.of(resultDto));

            // Assert
            assertThat(result.getResults()).containsKey(matchId);
        }

        @Test
        @DisplayName("Should preserve all match result data")
        void shouldPreserveAllMatchResultData() {
            // Arrange
            UUID matchId = UUID.randomUUID();
            MatchResultDto originalDto = MatchResultDto.builder()
                    .id(matchId)
                    .homeGoals(2)
                    .awayGoals(1)
                    .build();

            // Act
            MatchResultMapDto result = mapper.toDto(List.of(originalDto));
            MatchResultDto mappedDto = result.getResults().get(matchId);

            // Assert
            assertThat(mappedDto)
                    .isEqualTo(originalDto)
                    .satisfies(dto -> {
                        assertThat(dto.getId()).isEqualTo(matchId);
                        assertThat(dto.getHomeGoals()).isEqualTo(2);
                        assertThat(dto.getAwayGoals()).isEqualTo(1);
                    });
        }
    }

    @Nested
    @DisplayName("Mapping Multiple Results")
    class MappingMultipleResults {

        @Test
        @DisplayName("Should map multiple match results correctly")
        void shouldMapMultipleMatchResultsCorrectly() {
            // Arrange
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();
            UUID matchId3 = UUID.randomUUID();

            List<MatchResultDto> results = List.of(
                    MatchResultDto.builder().id(matchId1).homeGoals(2).awayGoals(1).build(),
                    MatchResultDto.builder().id(matchId2).homeGoals(0).awayGoals(0).build(),
                    MatchResultDto.builder().id(matchId3).homeGoals(3).awayGoals(2).build()
            );

            // Act
            MatchResultMapDto mappedResult = mapper.toDto(results);

            // Assert
            assertThat(mappedResult.getResults())
                    .hasSize(3)
                    .containsKeys(matchId1, matchId2, matchId3);
        }

        @Test
        @DisplayName("Should maintain correct mapping for each result")
        void shouldMaintainCorrectMappingForEachResult() {
            // Arrange
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();
            MatchResultDto dto1 = MatchResultDto.builder().id(matchId1).homeGoals(5).awayGoals(1).build();
            MatchResultDto dto2 = MatchResultDto.builder().id(matchId2).homeGoals(1).awayGoals(1).build();

            // Act
            MatchResultMapDto result = mapper.toDto(List.of(dto1, dto2));

            // Assert
            Map<UUID, MatchResultDto> resultsMap = result.getResults();
            assertThat(resultsMap.get(matchId1)).isEqualTo(dto1);
            assertThat(resultsMap.get(matchId2)).isEqualTo(dto2);
        }

        @Test
        @DisplayName("Should handle many results")
        void shouldHandleManyResults() {
            // Arrange
            List<MatchResultDto> results = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                results.add(MatchResultDto.builder()
                        .id(UUID.randomUUID())
                        .homeGoals(i % 5)
                        .awayGoals(i % 4)
                        .build());
            }

            // Act
            MatchResultMapDto mappedResult = mapper.toDto(results);

            // Assert
            assertThat(mappedResult.getResults()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("Mapping Empty Results")
    class MappingEmptyResults {

        @Test
        @DisplayName("Should handle empty results list")
        void shouldHandleEmptyResultsList() {
            // Arrange
            List<MatchResultDto> emptyResults = Collections.emptyList();

            // Act
            MatchResultMapDto result = mapper.toDto(emptyResults);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(dto -> assertThat(dto.getResults()).isEmpty());
        }

        @Test
        @DisplayName("Should return non-null DTO for empty input")
        void shouldReturnNonNullDtoForEmptyInput() {
            // Act
            MatchResultMapDto result = mapper.toDto(Collections.emptyList());

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getResults()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesAndSpecialScenarios {

        @Test
        @DisplayName("Should handle results with zero goals")
        void shouldHandleResultsWithZeroGoals() {
            // Arrange
            UUID matchId = UUID.randomUUID();
            MatchResultDto zeroGoalsDraw = MatchResultDto.builder()
                    .id(matchId)
                    .homeGoals(0)
                    .awayGoals(0)
                    .build();

            // Act
            MatchResultMapDto result = mapper.toDto(List.of(zeroGoalsDraw));

            // Assert
            assertThat(result.getResults().get(matchId).getHomeGoals()).isZero();
            assertThat(result.getResults().get(matchId).getAwayGoals()).isZero();
        }

        @Test
        @DisplayName("Should handle high goal scores")
        void shouldHandleHighGoalScores() {
            // Arrange
            UUID matchId = UUID.randomUUID();
            MatchResultDto highScoreMatch = MatchResultDto.builder()
                    .id(matchId)
                    .homeGoals(10)
                    .awayGoals(9)
                    .build();

            // Act
            MatchResultMapDto result = mapper.toDto(List.of(highScoreMatch));

            // Assert
            assertThat(result.getResults().get(matchId).getHomeGoals()).isEqualTo(10);
            assertThat(result.getResults().get(matchId).getAwayGoals()).isEqualTo(9);
        }

        @Test
        @DisplayName("Should handle null results list gracefully")
        void shouldHandleNullResultsListGracefully() {
            // Act & Assert
            assertThatThrownBy(() -> mapper.toDto(null))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should map each result exactly once")
        void shouldMapEachResultExactlyOnce() {
            // Arrange
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();
            MatchResultDto dto1 = MatchResultDto.builder().id(matchId1).homeGoals(1).awayGoals(0).build();
            MatchResultDto dto2 = MatchResultDto.builder().id(matchId2).homeGoals(0).awayGoals(1).build();

            // Act
            MatchResultMapDto result = mapper.toDto(List.of(dto1, dto2));

            // Assert
            assertThat(result.getResults()).hasSize(2);
            assertThat(result.getResults().values()).containsExactlyInAnyOrder(dto1, dto2);
        }

        @Test
        @DisplayName("Should be idempotent")
        void shouldBeIdempotent() {
            // Arrange
            List<MatchResultDto> results = List.of(
                    MatchResultDto.builder().id(UUID.randomUUID()).homeGoals(2).awayGoals(1).build()
            );

            // Act
            MatchResultMapDto result1 = mapper.toDto(results);
            MatchResultMapDto result2 = mapper.toDto(results);

            // Assert
            assertThat(result1.getResults()).isEqualTo(result2.getResults());
        }

        @Test
        @DisplayName("Should handle different goal combinations")
        void shouldHandleDifferentGoalCombinations() {
            // Arrange
            List<MatchResultDto> results = List.of(
                    MatchResultDto.builder().id(UUID.randomUUID()).homeGoals(0).awayGoals(1).build(),
                    MatchResultDto.builder().id(UUID.randomUUID()).homeGoals(1).awayGoals(0).build(),
                    MatchResultDto.builder().id(UUID.randomUUID()).homeGoals(2).awayGoals(2).build(),
                    MatchResultDto.builder().id(UUID.randomUUID()).homeGoals(3).awayGoals(1).build()
            );

            // Act
            MatchResultMapDto result = mapper.toDto(results);

            // Assert
            assertThat(result.getResults()).hasSize(4);
            assertThat(result.getResults().values())
                    .allSatisfy(dto -> {
                        assertThat(dto.getHomeGoals()).isGreaterThanOrEqualTo(0);
                        assertThat(dto.getAwayGoals()).isGreaterThanOrEqualTo(0);
                    });
        }
    }
}

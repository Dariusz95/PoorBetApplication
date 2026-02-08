package com.poorbet.matchservice.match.matchpool.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.matchservice.fixture.MatchFixtures;
import com.poorbet.matchservice.match.client.SimulationClient;
import com.poorbet.matchservice.match.client.TeamsClient;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.dto.TeamStatsDto;
import com.poorbet.matchservice.match.match.repository.MatchRepository;


@ExtendWith(MockitoExtension.class)
@DisplayName("MatchPoolSimulationServiceImpl Unit Tests")
class MatchPoolSimulationServiceImplTest {

    @Mock
    private LiveMatchSimulationManager liveManager;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchFinishService matchFinishService;

    @Mock
    private SimulationClient simulationClient;

    @Mock
    private TeamsClient teamsClient;

    @InjectMocks
    private MatchPoolSimulationServiceImpl matchPoolSimulationService;

    private UUID testPoolId;
    private List<Match> testMatches;
    private List<TeamStatsDto> testTeamStats;

    @BeforeEach
    void setUp() {
        testPoolId = UUID.randomUUID();
        testMatches = new ArrayList<>();
        testTeamStats = new ArrayList<>();
    }

    @Nested
    @DisplayName("Start Pool Simulation")
    class StartPoolSimulation {

        @Test
        @DisplayName("Should retrieve matches for pool")
        void shouldRetrieveMatchesForPool() {
            // Arrange
            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(Collections.emptyList());

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert
            verify(matchRepository).findByPoolId(testPoolId);
        }

        @Test
        @DisplayName("Should retrieve team stats for all teams in pool")
        void shouldRetrieveTeamStatsForAllTeamsInPool() {
            // Arrange
            UUID homeTeamId1 = UUID.randomUUID();
            UUID awayTeamId1 = UUID.randomUUID();
            UUID homeTeamId2 = UUID.randomUUID();
            UUID awayTeamId2 = UUID.randomUUID();

            Match match1 = createTestMatch(homeTeamId1, awayTeamId1);
            Match match2 = createTestMatch(homeTeamId2, awayTeamId2);

            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(match1, match2));
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(Collections.emptyList());

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert
            ArgumentCaptor<List<UUID>> teamIdsCaptor = ArgumentCaptor.forClass(List.class);
            verify(teamsClient).getStatsByIds(teamIdsCaptor.capture());

            List<UUID> capturedTeamIds = teamIdsCaptor.getValue();
            assertThat(capturedTeamIds)
                    .contains(homeTeamId1, awayTeamId1, homeTeamId2, awayTeamId2)
                    .hasSize(4);
        }

        @Test
        @DisplayName("Should not retrieve duplicated team stats")
        void shouldNotRetrieveDuplicatedTeamStats() {
            // Arrange
            UUID teamId = UUID.randomUUID();
            Match match1 = createTestMatch(teamId, UUID.randomUUID());
            Match match2 = createTestMatch(teamId, UUID.randomUUID());

            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(match1, match2));
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(Collections.emptyList());

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert
            ArgumentCaptor<List<UUID>> teamIdsCaptor = ArgumentCaptor.forClass(List.class);
            verify(teamsClient).getStatsByIds(teamIdsCaptor.capture());

            List<UUID> capturedTeamIds = teamIdsCaptor.getValue();
            assertThat(capturedTeamIds).doesNotHaveDuplicates();
        }


        @Test
        @DisplayName("Should handle single match in pool")
        void shouldHandleSingleMatchInPool() {
            // Arrange
            Match singleMatch = createTestMatch();
            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(singleMatch));
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(createTestTeamStats(2));

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert
            verify(matchRepository).findByPoolId(testPoolId);
            verify(teamsClient).getStatsByIds(any());
        }

        @Test
        @DisplayName("Should handle multiple matches in pool")
        void shouldHandleMultipleMatchesInPool() {
            // Arrange
            List<Match> multipleMatches = List.of(
                    createTestMatch(),
                    createTestMatch(),
                    createTestMatch()
            );
            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(multipleMatches);
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(createTestTeamStats(6));

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert
            verify(matchRepository).findByPoolId(testPoolId);
        }
    }

    @Nested
    @DisplayName("Team Stats Processing")
    class TeamStatsProcessing {

        @Test
        @DisplayName("Should collect team stats by team ID")
        void shouldCollectTeamStatsById() {
            // Arrange
            UUID homeTeamId = UUID.randomUUID();
            UUID awayTeamId = UUID.randomUUID();

            Match match = createTestMatch(homeTeamId, awayTeamId);
            TeamStatsDto homeStats = createTestTeamStats(homeTeamId);
            TeamStatsDto awayStats = createTestTeamStats(awayTeamId);

            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(match));
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(List.of(homeStats, awayStats));

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert
            verify(teamsClient).getStatsByIds(any());
        }

        @Test
        @DisplayName("Should handle missing team stats gracefully")
        void shouldHandleMissingTeamStatsGracefully() {
            // Arrange
            Match match = createTestMatch();
            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(match));
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(Collections.emptyList());

            // Act & Assert - Should not throw exception
            matchPoolSimulationService.startPoolSimulation(testPoolId);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle repository exception")
        void shouldHandleRepositoryException() {
            // Arrange
            when(matchRepository.findByPoolId(testPoolId))
                    .thenThrow(new RuntimeException("Repository error"));

            // Act & Assert
            assertThatThrownBy(() -> matchPoolSimulationService.startPoolSimulation(testPoolId))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should handle teams client exception")
        void shouldHandleTeamsClientException() {
            // Arrange
            Match match = createTestMatch();
            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(match));
            when(teamsClient.getStatsByIds(any()))
                    .thenThrow(new RuntimeException("Teams client error"));

            // Act & Assert
            assertThatThrownBy(() -> matchPoolSimulationService.startPoolSimulation(testPoolId))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("Async Processing")
    class AsyncProcessing {

        @Test
        @DisplayName("Should use Flux for reactive processing")
        void shouldUseFluxForReactiveProcessing() {
            // Arrange
            Match match = createTestMatch();
            when(matchRepository.findByPoolId(testPoolId))
                    .thenReturn(List.of(match));
            when(teamsClient.getStatsByIds(any()))
                    .thenReturn(createTestTeamStats(2));

            // Act
            matchPoolSimulationService.startPoolSimulation(testPoolId);

            // Assert - Verify matches were processed
            verify(liveManager, atLeastOnce()).startIfNotRunning(any());
        }
    }

    // Helper methods
    private Match createTestMatch() {
        Match match = MatchFixtures.createMatch();
        match.setStatus(MatchStatus.LIVE);
        return match;
    }

    private Match createTestMatch(UUID homeTeamId, UUID awayTeamId) {
        Match match = MatchFixtures.createMatch(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.LIVE);
        return match;
    }

    private TeamStatsDto createTestTeamStats(UUID teamId) {
        return MatchFixtures.createTeamStats(teamId);
    }

    private List<TeamStatsDto> createTestTeamStats(int count) {
        return MatchFixtures.createTeamStatsList(count);
    }
}

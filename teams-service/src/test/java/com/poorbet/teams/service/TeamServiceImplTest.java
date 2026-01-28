package com.poorbet.teams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.poorbet.teams.BaseServiceTest;
import com.poorbet.teams.TeamFixtures;
import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.exception.TeamNotFoundException;
import com.poorbet.teams.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

/**
 * Unit tests for TeamServiceImpl.
 * Uses mocked repository to test business logic in isolation.
 */
class TeamServiceImplTest extends BaseServiceTest {

    @InjectMocks
    private TeamServiceImpl teamService;

    @Override
    @BeforeEach
    protected void setUp() {
        // Service is already injected by @InjectMocks
    }

    // ==================== findRandomTeams Tests ====================

    @Test
    void findRandomTeams_shouldReturnListOfTeamsWithRequestedCount() {
        // Arrange
        int count = 3;
        List<TeamStatsDto> expectedTeams = List.of(
                TeamFixtures.interMiamiStats(),
                TeamFixtures.barcelonaStats(),
                TeamFixtures.psgStats()
        );

        when(teamRepository.findRandomTeams(count)).thenReturn(expectedTeams);

        // Act
        List<TeamStatsDto> result = teamService.findRandomTeams(count);

        // Assert
        assertEquals(3, result.size());
        assertEquals(expectedTeams, result);
        verify(teamRepository, times(1)).findRandomTeams(count);
    }

    @Test
    void findRandomTeams_shouldReturnSingleTeam() {
        // Arrange
        int count = 1;
        List<TeamStatsDto> expectedTeams = List.of(
                TeamFixtures.manchesterUnitedStats()
        );

        when(teamRepository.findRandomTeams(count)).thenReturn(expectedTeams);

        // Act
        List<TeamStatsDto> result = teamService.findRandomTeams(count);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TeamFixtures.MANCHESTER_UNITED, result.get(0).getName());
        verify(teamRepository, times(1)).findRandomTeams(count);
    }

    @Test
    void findRandomTeams_shouldThrowException_whenLessThanRequestedTeamsInDatabase() {
        // Arrange
        int requestedCount = 5;
        List<TeamStatsDto> availableTeams = List.of(
                TeamFixtures.interMiamiStats(),
                TeamFixtures.barcelonaStats()
        );

        when(teamRepository.findRandomTeams(requestedCount)).thenReturn(availableTeams);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> teamService.findRandomTeams(requestedCount));
        verify(teamRepository, times(1)).findRandomTeams(requestedCount);
    }

    @Test
    void findRandomTeams_shouldReturnEmptyList_whenRepositoryReturnsEmptyList() {
        // Arrange
        int count = 3;
        when(teamRepository.findRandomTeams(count)).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> teamService.findRandomTeams(count));
        verify(teamRepository, times(1)).findRandomTeams(count);
    }

    // ==================== getStatsByIds Tests ====================

    @Test
    void getStatsByIds_shouldReturnTeamsStats_whenIdsExist() {
        // Arrange
        List<java.util.UUID> ids = List.of(TeamFixtures.LIVERPOOL_ID, TeamFixtures.INTER_MIAMI_ID);
        List<Team> teams = List.of(
                TeamFixtures.liverpool(),
                TeamFixtures.interMiami()
        );

        when(teamRepository.findAllById(ids)).thenReturn(teams);

        // Act
        List<TeamStatsDto> result = teamService.getStatsByIds(ids);

        // Assert
        assertEquals(2, result.size());
        assertEquals(TeamFixtures.LIVERPOOL, result.get(0).getName());
        assertEquals(82, result.get(0).getAttackPower());
        assertEquals(80, result.get(0).getDefencePower());
        assertEquals(TeamFixtures.INTER_MIAMI, result.get(1).getName());
        assertEquals(72, result.get(1).getAttackPower());
        assertEquals(70, result.get(1).getDefencePower());
        verify(teamRepository, times(1)).findAllById(ids);
    }

    @Test
    void getStatsByIds_shouldReturnEmptyList_whenNoTeamsFound() {
        // Arrange
        List<java.util.UUID> ids = List.of(TeamFixtures.LIVERPOOL_ID, TeamFixtures.INTER_MIAMI_ID);
        when(teamRepository.findAllById(ids)).thenReturn(new ArrayList<>());

        // Act
        List<TeamStatsDto> result = teamService.getStatsByIds(ids);

        // Assert
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(teamRepository, times(1)).findAllById(ids);
    }

    @Test
    void getStatsByIds_shouldReturnPartialResults_whenOnlyPartialTeamsFound() {
        // Arrange
        List<java.util.UUID> ids = List.of(TeamFixtures.CHELSEA_ID, TeamFixtures.BARCELONA_ID, TeamFixtures.PSG_ID);
        List<Team> teams = List.of(
                TeamFixtures.chelsea()
        );

        when(teamRepository.findAllById(ids)).thenReturn(teams);

        // Act
        List<TeamStatsDto> result = teamService.getStatsByIds(ids);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TeamFixtures.CHELSEA, result.get(0).getName());
        verify(teamRepository, times(1)).findAllById(ids);
    }

    @Test
    void getStatsByIds_shouldMapAllTeamPropertiesCorrectly() {
        // Arrange
        List<java.util.UUID> ids = List.of(TeamFixtures.MANCHESTER_UNITED_ID);
        Team team = TeamFixtures.manchesterUnited();

        when(teamRepository.findAllById(ids)).thenReturn(List.of(team));

        // Act
        List<TeamStatsDto> result = teamService.getStatsByIds(ids);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TeamFixtures.MANCHESTER_UNITED_ID, result.get(0).getId());
        assertEquals(TeamFixtures.MANCHESTER_UNITED, result.get(0).getName());
        assertEquals(78, result.get(0).getAttackPower());
        assertEquals(75, result.get(0).getDefencePower());
        verify(teamRepository, times(1)).findAllById(ids);
    }

    // ==================== getById Tests ====================

    @Test
    void getById_shouldReturnTeamShortDto_whenTeamExists() {
        // Arrange
        Team team = TeamFixtures.barcelona();

        when(teamRepository.findById(TeamFixtures.BARCELONA_ID)).thenReturn(Optional.of(team));

        // Act
        TeamShortDto result = teamService.getById(TeamFixtures.BARCELONA_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TeamFixtures.BARCELONA_ID, result.id());
        assertEquals(TeamFixtures.BARCELONA, result.name());
        verify(teamRepository, times(1)).findById(TeamFixtures.BARCELONA_ID);
    }

    @Test
    void getById_shouldThrowTeamNotFoundException_whenTeamNotFound() {
        // Arrange
        when(teamRepository.findById(TeamFixtures.BARCELONA_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TeamNotFoundException.class, () -> teamService.getById(TeamFixtures.BARCELONA_ID));
        verify(teamRepository, times(1)).findById(TeamFixtures.BARCELONA_ID);
    }

    @Test
    void getById_shouldUseCache_onSecondCall() {
        // Arrange
        Team team = TeamFixtures.psg();

        when(teamRepository.findById(TeamFixtures.PSG_ID)).thenReturn(Optional.of(team));

        // Act
        TeamShortDto result1 = teamService.getById(TeamFixtures.PSG_ID);
        TeamShortDto result2 = teamService.getById(TeamFixtures.PSG_ID);

        // Assert
        assertEquals(result1, result2);
        assertEquals(TeamFixtures.PSG, result1.name());
        // Cache should be used, so repository is called once due to @Cacheable
        verify(teamRepository, times(1)).findById(TeamFixtures.PSG_ID);
    }

    @Test
    void getById_shouldReturnCorrectTeamShortDto_withIdAndNameOnly() {
        // Arrange
        Team team = TeamFixtures.chelsea();

        when(teamRepository.findById(TeamFixtures.CHELSEA_ID)).thenReturn(Optional.of(team));

        // Act
        TeamShortDto result = teamService.getById(TeamFixtures.CHELSEA_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TeamFixtures.CHELSEA_ID, result.id());
        assertEquals(TeamFixtures.CHELSEA, result.name());
        verify(teamRepository, times(1)).findById(TeamFixtures.CHELSEA_ID);
    }

    @Test
    void getById_shouldThrowException_withValidErrorMessage() {
        // Arrange
        when(teamRepository.findById(TeamFixtures.INTER_MIAMI_ID)).thenReturn(Optional.empty());

        // Act & Assert
        TeamNotFoundException exception = assertThrows(TeamNotFoundException.class,
                () -> teamService.getById(TeamFixtures.INTER_MIAMI_ID));
        assertTrue(exception.getMessage().contains(TeamFixtures.INTER_MIAMI_ID.toString()));
        assertTrue(exception.getMessage().contains("not found"));
        verify(teamRepository, times(1)).findById(TeamFixtures.INTER_MIAMI_ID);
    }
}

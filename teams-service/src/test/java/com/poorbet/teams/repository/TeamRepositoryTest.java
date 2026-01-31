package com.poorbet.teams.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.fixture.TeamFixtures;
import com.poorbet.teams.model.Team;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class TeamRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("teams_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
        initializeTestData();
    }

    private void initializeTestData() {
        List<Team> teams = List.of(
                TeamFixtures.manchesterUnited(),
                TeamFixtures.liverpool(),
                TeamFixtures.chelsea(),
                TeamFixtures.interMiami(),
                TeamFixtures.barcelona(),
                TeamFixtures.psg()
        );
        teamRepository.saveAll(teams);
    }

    @Test
    void findRandomTeams_shouldReturnRequestedNumberOfTeams() {
        // Act
        List<TeamStatsDto> result = teamRepository.findRandomTeams(2);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void findRandomTeams_shouldReturnAllTeams_whenCountEqualsTotal() {
        // Act
        List<TeamStatsDto> result = teamRepository.findRandomTeams(6);

        // Assert
        assertEquals(6, result.size());
    }

    @Test
    void findRandomTeams_shouldReturnTeamStatsDtoWithCorrectFields() {
        // Act
        List<TeamStatsDto> result = teamRepository.findRandomTeams(1);

        // Assert
        assertEquals(1, result.size());
        TeamStatsDto teamStats = result.get(0);
        assertNotNull(teamStats.getId());
        assertNotNull(teamStats.getName());
        assertNotEquals(0, teamStats.getAttackPower());
        assertNotEquals(0, teamStats.getDefencePower());
    }

    @Test
    void findRandomTeams_shouldNotReturnDuplicates() {
        // Act
        List<TeamStatsDto> result = teamRepository.findRandomTeams(6);

        // Assert
        assertEquals(6, result.size());
        long uniqueIds = result.stream().map(TeamStatsDto::getId).distinct().count();
        assertEquals(6, uniqueIds);
    }

    @Test
    void findById_shouldReturnTeam_whenTeamExists() {
        // Act
        Optional<Team> result = teamRepository.findById(TeamFixtures.MANCHESTER_UNITED_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TeamFixtures.MANCHESTER_UNITED, result.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenTeamDoesNotExist() {
        // Act
        Optional<Team> result = teamRepository.findById(UUID.randomUUID());

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectTeamData() {
        // Act
        Optional<Team> result = teamRepository.findById(TeamFixtures.LIVERPOOL_ID);

        // Assert
        assertTrue(result.isPresent());
        Team team = result.get();
        assertEquals(TeamFixtures.LIVERPOOL_ID, team.getId());
        assertEquals(TeamFixtures.LIVERPOOL, team.getName());
        assertEquals(82, team.getAttackPower());
        assertEquals(80, team.getDefencePower());
    }

    // ==================== findAllById Tests ====================

    @Test
    void findAllById_shouldReturnMultipleTeams_whenAllExist() {
        // Arrange
        List<UUID> ids = List.of(TeamFixtures.MANCHESTER_UNITED_ID, TeamFixtures.LIVERPOOL_ID);

        // Act
        List<Team> result = teamRepository.findAllById(ids);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().map(Team::getId).toList().contains(TeamFixtures.MANCHESTER_UNITED_ID));
        assertTrue(result.stream().map(Team::getId).toList().contains(TeamFixtures.LIVERPOOL_ID));
    }

    @Test
    void findAllById_shouldReturnOnlyExistingTeams_whenSomeDoNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        List<UUID> ids = List.of(TeamFixtures.MANCHESTER_UNITED_ID, nonExistentId, TeamFixtures.CHELSEA_ID);

        // Act
        List<Team> result = teamRepository.findAllById(ids);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().map(Team::getId).toList().contains(TeamFixtures.MANCHESTER_UNITED_ID));
        assertTrue(result.stream().map(Team::getId).toList().contains(TeamFixtures.CHELSEA_ID));
        assertFalse(result.stream().map(Team::getId).toList().contains(nonExistentId));
    }

    @Test
    void findAllById_shouldReturnEmpty_whenNoneExist() {
        // Arrange
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Act
        List<Team> result = teamRepository.findAllById(ids);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllById_shouldReturnAllTeams_whenEmptyListProvided() {
        // Act
        List<Team> result = teamRepository.findAllById(List.of());

        // Assert
        assertTrue(result.isEmpty());
    }

    // ==================== CRUD Tests ====================

    @Test
    void save_shouldPersistNewTeam() {
        // Arrange
        UUID newTeamId = UUID.randomUUID();
        Team newTeam = Team.builder()
                .id(newTeamId)
                .name("Arsenal")
                .img("arsenal.png")
                .attackPower(79)
                .defencePower(77)
                .build();

        // Act
        Team savedTeam = teamRepository.save(newTeam);

        // Assert
        assertNotNull(savedTeam);
        assertEquals(newTeamId, savedTeam.getId());
        assertEquals("Arsenal", savedTeam.getName());
        assertTrue(teamRepository.existsById(newTeamId));
    }

    @Test
    void save_shouldUpdateExistingTeam() {
        // Arrange
        Optional<Team> existingTeam = teamRepository.findById(TeamFixtures.MANCHESTER_UNITED_ID);
        assertTrue(existingTeam.isPresent());
        Team teamToUpdate = existingTeam.get();
        teamToUpdate.setName("Manchester City");
        teamToUpdate.setAttackPower(85);

        // Act
        Team updatedTeam = teamRepository.save(teamToUpdate);

        // Assert
        assertEquals("Manchester City", updatedTeam.getName());
        assertEquals(85, updatedTeam.getAttackPower());
        Optional<Team> verifyUpdate = teamRepository.findById(TeamFixtures.MANCHESTER_UNITED_ID);
        assertTrue(verifyUpdate.isPresent());
        assertEquals("Manchester City", verifyUpdate.get().getName());
    }

    @Test
    void delete_shouldRemoveTeam() {
        // Arrange
        assertTrue(teamRepository.existsById(TeamFixtures.MANCHESTER_UNITED_ID));

        // Act
        teamRepository.deleteById(TeamFixtures.MANCHESTER_UNITED_ID);

        // Assert
        assertFalse(teamRepository.existsById(TeamFixtures.MANCHESTER_UNITED_ID));
    }

    @Test
    void findAll_shouldReturnAllTeams() {
        // Act
        List<Team> result = teamRepository.findAll();

        // Assert
        assertEquals(6, result.size());
    }

    @Test
    void count_shouldReturnCorrectNumberOfTeams() {
        // Act
        long count = teamRepository.count();

        // Assert
        assertEquals(6, count);
    }

    @Test
    void existsById_shouldReturnTrue_whenTeamExists() {
        // Act
        boolean exists = teamRepository.existsById(TeamFixtures.MANCHESTER_UNITED_ID);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenTeamDoesNotExist() {
        // Act
        boolean exists = teamRepository.existsById(UUID.randomUUID());

        // Assert
        assertFalse(exists);
    }
}

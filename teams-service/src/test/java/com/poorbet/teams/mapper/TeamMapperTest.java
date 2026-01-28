package com.poorbet.teams.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.poorbet.teams.TeamFixtures;
import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamMapperTest {

    private TeamMapper teamMapper;

    @BeforeEach
    void setUp() {
        teamMapper = new TeamMapper();
    }

    // ==================== toDto Tests ====================

    @Test
    void toDto_shouldMapTeamToTeamStatsDto() {
        // Arrange
        Team team = TeamFixtures.manchesterUnited();

        // Act
        TeamStatsDto result = teamMapper.toDto(team);

        // Assert
        assertNotNull(result);
        assertEquals(TeamFixtures.MANCHESTER_UNITED_ID, result.getId());
        assertEquals(TeamFixtures.MANCHESTER_UNITED, result.getName());
        assertEquals(78, result.getAttackPower());
        assertEquals(75, result.getDefencePower());
    }

    @Test
    void toDto_shouldMapAllTeamProperties() {
        // Arrange
        Team team = TeamFixtures.liverpool();

        // Act
        TeamStatsDto result = teamMapper.toDto(team);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getName());
        assertTrue(result.getAttackPower() > 0);
        assertTrue(result.getDefencePower() > 0);
    }

    @Test
    void toDto_shouldPreserveExactValues() {
        // Arrange
        Team team = TeamFixtures.barcelona();

        // Act
        TeamStatsDto result = teamMapper.toDto(team);

        // Assert
        assertEquals(85, result.getAttackPower());
        assertEquals(72, result.getDefencePower());
        assertEquals(TeamFixtures.BARCELONA, result.getName());
    }

    @Test
    void toDto_shouldHandleMinimumPowers() {
        // Arrange
        Team team = Team.builder()
                .id(java.util.UUID.randomUUID())
                .name("Weak Team")
                .attackPower(1)
                .defencePower(1)
                .build();

        // Act
        TeamStatsDto result = teamMapper.toDto(team);

        // Assert
        assertEquals(1, result.getAttackPower());
        assertEquals(1, result.getDefencePower());
    }

    @Test
    void toDto_shouldHandleMaximumPowers() {
        // Arrange
        Team team = Team.builder()
                .id(java.util.UUID.randomUUID())
                .name("Strong Team")
                .attackPower(100)
                .defencePower(100)
                .build();

        // Act
        TeamStatsDto result = teamMapper.toDto(team);

        // Assert
        assertEquals(100, result.getAttackPower());
        assertEquals(100, result.getDefencePower());
    }

    // ==================== toTeamShortDto Tests ====================

    @Test
    void toTeamShortDto_shouldMapTeamToTeamShortDto() {
        // Arrange
        Team team = TeamFixtures.chelsea();

        // Act
        TeamShortDto result = TeamMapper.toTeamShortDto(team);

        // Assert
        assertNotNull(result);
        assertEquals(TeamFixtures.CHELSEA_ID, result.id());
        assertEquals(TeamFixtures.CHELSEA, result.name());
    }

    @Test
    void toTeamShortDto_shouldMapIdAndNameOnly() {
        // Arrange
        Team team = TeamFixtures.interMiami();

        // Act
        TeamShortDto result = TeamMapper.toTeamShortDto(team);

        // Assert
        assertNotNull(result);
        assertEquals(TeamFixtures.INTER_MIAMI_ID, result.id());
        assertEquals(TeamFixtures.INTER_MIAMI, result.name());
        // Verify it doesn't contain other fields
        assertEquals(2, result.getClass().getDeclaredFields().length);
    }

    @Test
    void toTeamShortDto_shouldNotIncludeAttackAndDefencePowers() {
        // Arrange
        Team team = TeamFixtures.barcelona();

        // Act
        TeamShortDto result = TeamMapper.toTeamShortDto(team);

        // Assert
        assertNotNull(result);
        // TeamShortDto should only have id and name
        assertEquals(2, result.getClass().getRecordComponents().length);
    }

    @Test
    void toTeamShortDto_shouldPreserveTeamId() {
        // Arrange
        Team team = TeamFixtures.psg();

        // Act
        TeamShortDto result = TeamMapper.toTeamShortDto(team);

        // Assert
        assertEquals(TeamFixtures.PSG_ID, result.id());
    }

    @Test
    void toTeamShortDto_shouldPreserveTeamName() {
        // Arrange
        Team team = TeamFixtures.liverpool();

        // Act
        TeamShortDto result = TeamMapper.toTeamShortDto(team);

        // Assert
        assertEquals(TeamFixtures.LIVERPOOL, result.name());
    }

    @Test
    void toTeamShortDto_shouldHandleSpecialCharactersInName() {
        // Arrange
        String teamName = "FC Köln";
        Team team = Team.builder()
                .id(java.util.UUID.randomUUID())
                .name(teamName)
                .img("koeln.png")
                .attackPower(70)
                .defencePower(72)
                .build();

        // Act
        TeamShortDto result = TeamMapper.toTeamShortDto(team);

        // Assert
        assertEquals(teamName, result.name());
    }

    // ==================== Mapping Consistency Tests ====================

    @Test
    void toDto_andToTeamShortDto_shouldConsistentlyMapId() {
        // Arrange
        Team team = TeamFixtures.manchesterUnited();

        // Act
        TeamStatsDto statsDto = teamMapper.toDto(team);
        TeamShortDto shortDto = TeamMapper.toTeamShortDto(team);

        // Assert
        assertEquals(statsDto.getId(), shortDto.id());
    }

    @Test
    void toDto_andToTeamShortDto_shouldConsistentlyMapName() {
        // Arrange
        Team team = TeamFixtures.chelsea();

        // Act
        TeamStatsDto statsDto = teamMapper.toDto(team);
        TeamShortDto shortDto = TeamMapper.toTeamShortDto(team);

        // Assert
        assertEquals(statsDto.getName(), shortDto.name());
    }
}

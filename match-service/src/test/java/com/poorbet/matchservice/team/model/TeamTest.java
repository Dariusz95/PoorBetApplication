package com.poorbet.matchservice.team.model;

import com.poorbet.matchservice.fixture.TeamFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    void shouldCreateTeamWithBuilder() {
        // Act
        Team team = TeamFixtures.manchesterUnited();

        // Assert
        assertNotNull(team);
        assertEquals(TeamFixtures.MANCHESTER_UNITED_ID, team.getId());
        assertEquals(TeamFixtures.MANCHESTER_UNITED, team.getName());
        assertEquals(78, team.getAttackPower());
        assertEquals(75, team.getDefencePower());
    }

    @Test
    void shouldCreateTeamWithNoArgsConstructor() {
        // Act
        Team team = new Team();

        // Assert
        assertNotNull(team);
        assertNull(team.getId());
        assertNull(team.getName());
        assertNull(team.getImg());
        assertEquals(0, team.getAttackPower());
        assertEquals(0, team.getDefencePower());
    }

    @Test
    void shouldCreateTeamWithAllArgsConstructor() {
        // Act
        Team team = TeamFixtures.liverpool();

        // Assert
        assertEquals(TeamFixtures.LIVERPOOL_ID, team.getId());
        assertEquals(TeamFixtures.LIVERPOOL, team.getName());
        assertEquals(82, team.getAttackPower());
        assertEquals(80, team.getDefencePower());
    }

    // ==================== Setter Tests ====================

    @Test
    void shouldSetId() {
        // Arrange
        Team team = new Team();
        UUID newId = UUID.randomUUID();

        // Act
        team.setId(newId);

        // Assert
        assertEquals(newId, team.getId());
    }

    @Test
    void shouldSetName() {
        // Arrange
        Team team = new Team();

        // Act
        team.setName(TeamFixtures.CHELSEA);

        // Assert
        assertEquals(TeamFixtures.CHELSEA, team.getName());
    }

    @Test
    void shouldSetImg() {
        // Arrange
        Team team = new Team();

        // Act
        team.setImg("chelsea.png");

        // Assert
        assertEquals("chelsea.png", team.getImg());
    }

    @Test
    void shouldSetAttackPower() {
        // Arrange
        Team team = new Team();

        // Act
        team.setAttackPower(76);

        // Assert
        assertEquals(76, team.getAttackPower());
    }

    @Test
    void shouldSetDefencePower() {
        // Arrange
        Team team = new Team();

        // Act
        team.setDefencePower(78);

        // Assert
        assertEquals(78, team.getDefencePower());
    }

    // ==================== Validation Tests ====================

    @Test
    void shouldAcceptPositiveAttackPower() {
        // Act
        Team team = TeamFixtures.createTeam(
                UUID.randomUUID(),
                "Arsenal",
                79,
                77);

        // Assert
        assertEquals(79, team.getAttackPower());
    }

    @Test
    void shouldAcceptPositiveDefencePower() {
        // Act
        Team team = TeamFixtures.barcelona();

        // Assert
        assertEquals(72, team.getDefencePower());
    }

    @Test
    void shouldAcceptZeroAttackPower() {
        // Act
        Team team = TeamFixtures.createTeam(
                UUID.randomUUID(),
                "Weak Team",
                0,
                50);

        // Assert
        assertEquals(0, team.getAttackPower());
    }

    @Test
    void shouldAcceptZeroDefencePower() {
        // Act
        Team team = TeamFixtures.createTeam(
                UUID.randomUUID(),
                "Weak Team",
                50,
                0);

        // Assert
        assertEquals(0, team.getDefencePower());
    }

    @Test
    void shouldAcceptHighAttackAndDefencePowers() {
        // Act
        Team team = TeamFixtures.createTeam(
                UUID.randomUUID(),
                "Strong Team",
                100,
                100);

        // Assert
        assertEquals(100, team.getAttackPower());
        assertEquals(100, team.getDefencePower());
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentIds() {
        // Arrange
        Team team1 = TeamFixtures.psg();
        Team team2 = TeamFixtures.createTeam(
                UUID.randomUUID(),
                TeamFixtures.PSG,
                80,
                75);

        // Assert
        assertNotEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentNames() {
        // Arrange
        UUID id = UUID.randomUUID();
        Team team1 = TeamFixtures.createTeam(id, "Bayern Munich", 82, 80);
        Team team2 = TeamFixtures.createTeam(id, "Dortmund", 82, 80);

        // Assert
        assertNotEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentAttackPower() {
        // Arrange
        UUID id = UUID.randomUUID();
        Team team1 = TeamFixtures.createTeam(id, "Juventus", 75, 78);
        Team team2 = TeamFixtures.createTeam(id, "Juventus", 80, 78);

        // Assert
        assertNotEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentDefencePower() {
        // Arrange
        UUID id = UUID.randomUUID();
        Team team1 = TeamFixtures.createTeam(id, "Ajax", 76, 72);
        Team team2 = TeamFixtures.createTeam(id, "Ajax", 76, 80);

        // Assert
        assertNotEquals(team1, team2);
    }

    // ==================== toString Tests ====================

    @Test
    void shouldGenerateToString() {
        // Arrange
        Team team = TeamFixtures.createTeam(UUID.randomUUID(), "AC Milan", 77, 76);

        // Act
        String toString = team.toString();

        // Assert
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("Team") || toString.contains("id") || toString.contains("name"));
    }

    // ==================== JPA Annotation Tests ====================

    @Test
    void shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(Team.class.isAnnotationPresent(jakarta.persistence.Entity.class));
    }

    @Test
    void shouldHaveTableAnnotation() {
        // Assert
        assertTrue(Team.class.isAnnotationPresent(jakarta.persistence.Table.class));
    }

    // ==================== Edge Case Tests ====================

    @Test
    void shouldHandleNullImageUrl() {
        // Act
        Team team = Team.builder()
                .id(UUID.randomUUID())
                .name("Unknown Team")
                .img(null)
                .attackPower(50)
                .defencePower(50)
                .build();

        // Assert
        assertNull(team.getImg());
    }

    @Test
    void shouldHandleEmptyStringName() {
        // Act
        Team team = Team.builder()
                .id(UUID.randomUUID())
                .name("")
                .img("empty.png")
                .attackPower(50)
                .defencePower(50)
                .build();

        // Assert
        assertEquals("", team.getName());
    }

    @Test
    void shouldAllowTeamNameUpdate() {
        // Arrange
        Team team = TeamFixtures.createTeam(UUID.randomUUID(), "Original Name", 60, 60);

        // Act
        team.setName("Updated Name");

        // Assert
        assertEquals("Updated Name", team.getName());
    }

    @Test
    void shouldAllowMultipleUpdates() {
        // Arrange
        Team team = TeamFixtures.createTeam(UUID.randomUUID(), "Team A", 50, 50);

        // Act
        team.setName("Team B");
        team.setAttackPower(75);
        team.setDefencePower(80);

        // Assert
        assertEquals("Team B", team.getName());
        assertEquals(75, team.getAttackPower());
        assertEquals(80, team.getDefencePower());
    }
}

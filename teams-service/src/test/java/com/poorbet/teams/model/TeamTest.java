package com.poorbet.teams.model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TeamTest {

    private final UUID testTeamId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void shouldCreateTeamWithBuilder() {
        // Act
        Team team = Team.builder()
                .id(testTeamId)
                .name("Manchester United")
                .img("manchester-united.png")
                .attackPower(78)
                .defencePower(75)
                .build();

        // Assert
        assertNotNull(team);
        assertEquals(testTeamId, team.getId());
        assertEquals("Manchester United", team.getName());
        assertEquals("manchester-united.png", team.getImg());
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
        Team team = new Team(testTeamId, "Liverpool", "liverpool.png", 82, 80);

        // Assert
        assertEquals(testTeamId, team.getId());
        assertEquals("Liverpool", team.getName());
        assertEquals("liverpool.png", team.getImg());
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
        team.setName("Chelsea");

        // Assert
        assertEquals("Chelsea", team.getName());
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
        Team team = Team.builder()
                .id(testTeamId)
                .name("Arsenal")
                .attackPower(79)
                .defencePower(77)
                .build();

        // Assert
        assertEquals(79, team.getAttackPower());
    }

    @Test
    void shouldAcceptPositiveDefencePower() {
        // Act
        Team team = Team.builder()
                .id(testTeamId)
                .name("Barcelona")
                .attackPower(85)
                .defencePower(70)
                .build();

        // Assert
        assertEquals(70, team.getDefencePower());
    }

    @Test
    void shouldAcceptZeroAttackPower() {
        // Act
        Team team = Team.builder()
                .id(testTeamId)
                .name("Weak Team")
                .attackPower(0)
                .defencePower(50)
                .build();

        // Assert
        assertEquals(0, team.getAttackPower());
    }

    @Test
    void shouldAcceptZeroDefencePower() {
        // Act
        Team team = Team.builder()
                .id(testTeamId)
                .name("Weak Team")
                .attackPower(50)
                .defencePower(0)
                .build();

        // Assert
        assertEquals(0, team.getDefencePower());
    }

    @Test
    void shouldAcceptHighAttackAndDefencePowers() {
        // Act
        Team team = Team.builder()
                .id(testTeamId)
                .name("Strong Team")
                .attackPower(100)
                .defencePower(100)
                .build();

        // Assert
        assertEquals(100, team.getAttackPower());
        assertEquals(100, team.getDefencePower());
    }

    // ==================== Equality Tests ====================

    @Test
    void shouldBeEqual_whenTeamsHaveSameData() {
        // Arrange
        Team team1 = Team.builder()
                .id(testTeamId)
                .name("Real Madrid")
                .img("real-madrid.png")
                .attackPower(88)
                .defencePower(85)
                .build();

        Team team2 = Team.builder()
                .id(testTeamId)
                .name("Real Madrid")
                .img("real-madrid.png")
                .attackPower(88)
                .defencePower(85)
                .build();

        // Assert
        assertEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentIds() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Team team1 = Team.builder()
                .id(id1)
                .name("PSG")
                .img("psg.png")
                .attackPower(80)
                .defencePower(75)
                .build();

        Team team2 = Team.builder()
                .id(id2)
                .name("PSG")
                .img("psg.png")
                .attackPower(80)
                .defencePower(75)
                .build();

        // Assert
        assertNotEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentNames() {
        // Arrange
        Team team1 = Team.builder()
                .id(testTeamId)
                .name("Bayern Munich")
                .img("bayern.png")
                .attackPower(82)
                .defencePower(80)
                .build();

        Team team2 = Team.builder()
                .id(testTeamId)
                .name("Dortmund")
                .img("bayern.png")
                .attackPower(82)
                .defencePower(80)
                .build();

        // Assert
        assertNotEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentAttackPower() {
        // Arrange
        Team team1 = Team.builder()
                .id(testTeamId)
                .name("Juventus")
                .img("juventus.png")
                .attackPower(75)
                .defencePower(78)
                .build();

        Team team2 = Team.builder()
                .id(testTeamId)
                .name("Juventus")
                .img("juventus.png")
                .attackPower(80)
                .defencePower(78)
                .build();

        // Assert
        assertNotEquals(team1, team2);
    }

    @Test
    void shouldNotBeEqual_whenTeamsHaveDifferentDefencePower() {
        // Arrange
        Team team1 = Team.builder()
                .id(testTeamId)
                .name("Ajax")
                .img("ajax.png")
                .attackPower(76)
                .defencePower(72)
                .build();

        Team team2 = Team.builder()
                .id(testTeamId)
                .name("Ajax")
                .img("ajax.png")
                .attackPower(76)
                .defencePower(80)
                .build();

        // Assert
        assertNotEquals(team1, team2);
    }

    // ==================== toString Tests ====================

    @Test
    void shouldGenerateToString() {
        // Arrange
        Team team = Team.builder()
                .id(testTeamId)
                .name("AC Milan")
                .img("ac-milan.png")
                .attackPower(77)
                .defencePower(76)
                .build();

        // Act
        String toString = team.toString();

        // Assert
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("Team") || toString.contains("id") || toString.contains("name"));
    }

    // ==================== Hash Code Tests ====================

    @Test
    void shouldHaveSameHashCode_whenTeamsAreEqual() {
        // Arrange
        Team team1 = Team.builder()
                .id(testTeamId)
                .name("Inter Milan")
                .img("inter-milan.png")
                .attackPower(81)
                .defencePower(79)
                .build();

        Team team2 = Team.builder()
                .id(testTeamId)
                .name("Inter Milan")
                .img("inter-milan.png")
                .attackPower(81)
                .defencePower(79)
                .build();

        // Assert
        assertEquals(team1.hashCode(), team2.hashCode());
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
                .id(testTeamId)
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
                .id(testTeamId)
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
        Team team = Team.builder()
                .id(testTeamId)
                .name("Original Name")
                .img("original.png")
                .attackPower(60)
                .defencePower(60)
                .build();

        // Act
        team.setName("Updated Name");

        // Assert
        assertEquals("Updated Name", team.getName());
    }

    @Test
    void shouldAllowMultipleUpdates() {
        // Arrange
        Team team = Team.builder()
                .id(testTeamId)
                .name("Team A")
                .img("a.png")
                .attackPower(50)
                .defencePower(50)
                .build();

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

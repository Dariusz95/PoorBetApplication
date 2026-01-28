package com.poorbet.teams;

import java.util.UUID;

import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;

/**
 * Team test fixtures - centralized test data management.
 * 
 * Provides factory methods for creating consistent test data across all test classes.
 * This prevents data duplication and makes it easy to update team stats in one place.
 * 
 * Note: This is NOT a builder pattern - it's a fixture factory.
 * For flexibility, use the createTeam() method with custom parameters.
 */
public class TeamFixtures {

    // ==================== Team IDs ====================
    public static final UUID MANCHESTER_UNITED_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID LIVERPOOL_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    public static final UUID CHELSEA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    public static final UUID INTER_MIAMI_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");
    public static final UUID BARCELONA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");
    public static final UUID PSG_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");

    // ==================== Team Names ====================
    public static final String MANCHESTER_UNITED = "Manchester United";
    public static final String LIVERPOOL = "Liverpool";
    public static final String CHELSEA = "Chelsea";
    public static final String INTER_MIAMI = "Inter Miami";
    public static final String BARCELONA = "Barcelona";
    public static final String PSG = "PSG";

    // ==================== Team Stats (Power Values) ====================
    // Manchester United: Balanced team (78 attack, 75 defence)
    private static final int MU_ATTACK = 78;
    private static final int MU_DEFENCE = 75;

    // Liverpool: Strong offensive team (82 attack, 80 defence)
    private static final int LIV_ATTACK = 82;
    private static final int LIV_DEFENCE = 80;

    // Chelsea: Balanced team (76 attack, 78 defence)
    private static final int CHE_ATTACK = 76;
    private static final int CHE_DEFENCE = 78;

    // Inter Miami: Weaker team (72 attack, 70 defence)
    private static final int IM_ATTACK = 72;
    private static final int IM_DEFENCE = 70;

    // Barcelona: Strong offensive team (85 attack, 72 defence)
    private static final int BAR_ATTACK = 85;
    private static final int BAR_DEFENCE = 72;

    // PSG: Strong balanced team (80 attack, 75 defence)
    private static final int PSG_ATTACK = 80;
    private static final int PSG_DEFENCE = 75;

    // ==================== Factory Methods - Generic ====================

    /**
     * Creates a Team entity with specified values
     */
    public static Team createTeam(UUID id, String name, int attackPower, int defencePower) {
        return Team.builder()
                .id(id)
                .name(name)
                .attackPower(attackPower)
                .defencePower(defencePower)
                .build();
    }

    /**
     * Creates a TeamStatsDto with specified values
     */
    public static TeamStatsDto createTeamStatsDto(UUID id, String name, int attackPower, int defencePower) {
        return new TeamStatsDto(id, name, attackPower, defencePower);
    }

    /**
     * Creates a TeamShortDto with specified values
     */
    public static TeamShortDto createTeamShortDto(UUID id, String name) {
        return new TeamShortDto(id, name);
    }

    // ==================== Fixture Methods - Team Entities ====================
    // These methods create Team entities using the predefined constants above.
    // This eliminates data duplication and makes it easy to update stats globally.

    public static Team manchesterUnited() {
        return createTeam(MANCHESTER_UNITED_ID, MANCHESTER_UNITED, MU_ATTACK, MU_DEFENCE);
    }

    public static Team liverpool() {
        return createTeam(LIVERPOOL_ID, LIVERPOOL, LIV_ATTACK, LIV_DEFENCE);
    }

    public static Team chelsea() {
        return createTeam(CHELSEA_ID, CHELSEA, CHE_ATTACK, CHE_DEFENCE);
    }

    public static Team interMiami() {
        return createTeam(INTER_MIAMI_ID, INTER_MIAMI, IM_ATTACK, IM_DEFENCE);
    }

    public static Team barcelona() {
        return createTeam(BARCELONA_ID, BARCELONA, BAR_ATTACK, BAR_DEFENCE);
    }

    public static Team psg() {
        return createTeam(PSG_ID, PSG, PSG_ATTACK, PSG_DEFENCE);
    }

    // ==================== Fixture Methods - TeamStatsDtos ====================
    // These methods create DTOs from Team entities to avoid duplicating stats.
    // If Team stats change, DTO stats automatically update.

    public static TeamStatsDto manchesterUnitedStats() {
        Team team = manchesterUnited();
        return toStatsDto(team);
    }

    public static TeamStatsDto liverpoolStats() {
        Team team = liverpool();
        return toStatsDto(team);
    }

    public static TeamStatsDto chelseaStats() {
        Team team = chelsea();
        return toStatsDto(team);
    }

    public static TeamStatsDto interMiamiStats() {
        Team team = interMiami();
        return toStatsDto(team);
    }

    public static TeamStatsDto barcelonaStats() {
        Team team = barcelona();
        return toStatsDto(team);
    }

    public static TeamStatsDto psgStats() {
        Team team = psg();
        return toStatsDto(team);
    }

    // ==================== Fixture Methods - TeamShortDtos ====================

    public static TeamShortDto manchesterUnitedShort() {
        return createTeamShortDto(MANCHESTER_UNITED_ID, MANCHESTER_UNITED);
    }

    public static TeamShortDto liverpoolShort() {
        return createTeamShortDto(LIVERPOOL_ID, LIVERPOOL);
    }

    public static TeamShortDto chelseaShort() {
        return createTeamShortDto(CHELSEA_ID, CHELSEA);
    }

    public static TeamShortDto interMiamiShort() {
        return createTeamShortDto(INTER_MIAMI_ID, INTER_MIAMI);
    }

    public static TeamShortDto barcelonaShort() {
        return createTeamShortDto(BARCELONA_ID, BARCELONA);
    }

    public static TeamShortDto psgShort() {
        return createTeamShortDto(PSG_ID, PSG);
    }

    // ==================== Helper Methods ====================

    /**
     * Converts Team entity to TeamStatsDto
     */
    private static TeamStatsDto toStatsDto(Team team) {
        return createTeamStatsDto(team.getId(), team.getName(), team.getAttackPower(), team.getDefencePower());
    }
}


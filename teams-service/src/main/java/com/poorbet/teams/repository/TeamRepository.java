package com.poorbet.teams.repository;

import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    @Query(value = """
    SELECT id, name, attack_power, defence_power
    FROM teams
    ORDER BY RANDOM()
    LIMIT :limit
    """, nativeQuery = true)
    List<TeamStatsDto> findRandomTeams(@Param("limit") int limit);
}

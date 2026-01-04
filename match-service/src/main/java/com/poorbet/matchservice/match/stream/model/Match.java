package com.poorbet.matchservice.match.stream.model;

import java.util.UUID;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "match_id")
    private UUID matchId;

    @Column(name = "home_team_id", nullable = false)
    private UUID homeTeamId;

    @Column(name = "away_team_id", nullable = false)
    private UUID awayTeamId;
    
    @Column(name = "home_goals")
    private int homeGoals;
    
    @Column(name = "away_goals")
    private int awayGoals;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_pool_id", nullable = false)
    private MatchPool pool;
}

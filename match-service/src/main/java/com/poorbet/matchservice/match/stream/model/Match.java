package com.poorbet.matchservice.match.stream.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    
    @Column(name = "home_team_id")
    private UUID homeTeamId;
    
    @Column(name = "away_team_id")
    private UUID awayTeamId;
    
    @Column(name = "home_goals")
    private int homeGoals;
    
    @Column(name = "away_goals")
    private int awayGoals;
    
    @Column(name = "current_minute")
    private int currentMinute;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "match_pool_id", nullable = false)
    private MatchPool pool;
}

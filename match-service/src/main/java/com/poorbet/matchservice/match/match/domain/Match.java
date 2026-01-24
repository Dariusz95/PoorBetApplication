package com.poorbet.matchservice.match.match.domain;

import java.util.UUID;

import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
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
    private UUID id;

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
    @JoinColumn(name = "pool_id", nullable = false)
    private MatchPool pool;

    @OneToOne(
            mappedBy = "match",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Odds odds;

    public void setOdds(Odds odds) {
        this.odds = odds;
        odds.setMatch(this);
    }
}

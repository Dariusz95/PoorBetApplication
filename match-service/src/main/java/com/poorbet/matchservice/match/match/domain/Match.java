package com.poorbet.matchservice.match.match.domain;

import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "match")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

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

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}

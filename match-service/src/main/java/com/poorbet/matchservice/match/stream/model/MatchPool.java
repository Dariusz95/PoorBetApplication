package com.poorbet.matchservice.match.stream.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "match_pool")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPool {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PoolStatus status;

    @Column(name = "scheduled_start_time", nullable = false)
    private LocalDateTime scheduledStartTime;

    @OneToMany(
            mappedBy = "pool",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Match> matches = new ArrayList<>();

    public void addMatch(Match match) {
        matches.add(match);
        match.setPool(this);
    }
}

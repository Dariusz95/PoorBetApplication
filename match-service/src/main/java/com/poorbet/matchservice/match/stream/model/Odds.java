package com.poorbet.matchservice.match.stream.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "match_odds", uniqueConstraints = @UniqueConstraint(columnNames = "match_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Odds {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "home_win")
    private BigDecimal homeWin;

    @Column(name = "draw")
    private BigDecimal draw;

    @Column(name = "away_win")
    private BigDecimal awayWin;
}

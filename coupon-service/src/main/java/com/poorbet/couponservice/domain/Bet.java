package com.poorbet.couponservice.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID matchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BetStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BetType betType;

    @Column(nullable = false)
    private BigDecimal odds;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Version
    private Long version;

    public void settle(MatchResultEventDto result) {
        this.status = this.betType.mapToStatus(
                result,
                result.homeGoals(),
                result.awayGoals()
        );
    }
}

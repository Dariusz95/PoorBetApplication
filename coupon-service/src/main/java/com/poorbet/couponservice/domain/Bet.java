package com.poorbet.couponservice.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;

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
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false)
    private String homeTeamName;

    @Column(nullable = false)
    private String awayTeamName;

    @Column(nullable = false)
    private OffsetDateTime matchStartTime;

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

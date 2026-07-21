package com.poorbet.couponservice.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @DecimalMin("1.00")
    private BigDecimal stake;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponStatus status;

    @Column(nullable = false)
    private BigDecimal potentialPayout;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalOdds;

    @JsonManagedReference
    @OneToMany(mappedBy = "coupon", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bet> bets = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public void addBet(Bet bet) {
        bets.add(bet);
        bet.setCoupon(this);
    }

    public void recalculateStatus() {
        boolean allWon = true;

        for (Bet bet : bets) {
            BetStatus status = bet.getStatus();

            if (status == BetStatus.LOST) {
                this.status = CouponStatus.LOST;
                return;
            }

            if (status != BetStatus.WON) {
                allWon = false;
            }
        }

        this.status = allWon ? CouponStatus.WON : CouponStatus.OPEN;
    }
}

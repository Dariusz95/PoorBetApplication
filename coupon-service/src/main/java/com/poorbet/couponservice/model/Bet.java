package com.poorbet.couponservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.poorbet.couponservice.model.enums.BetStatus;
import com.poorbet.couponservice.model.enums.BetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table
@Data
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
}

package com.poorbet.couponservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.poorbet.couponservice.dto.CreateBetDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@Data
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

    @JsonManagedReference
    @OneToMany(mappedBy = "coupon", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bet> bets = new ArrayList<>();

    public void addBet(Bet bet) {
        bets.add(bet);
        bet.setCoupon(this);
    }
}

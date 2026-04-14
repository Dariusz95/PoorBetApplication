package com.poorbet.walletservice.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet_reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletReservation {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


    public static WalletReservation create(UUID id, UUID userId, BigDecimal amount) {

        return new WalletReservation(
                id,
                userId,
                amount,
                ReservationStatus.RESERVED
        );
    }

    private WalletReservation(UUID id, UUID userId, BigDecimal amount, ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

}
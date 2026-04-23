package com.poorbet.walletservice.repository;

import com.poorbet.walletservice.domain.model.WalletReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletReservationRepository extends JpaRepository<WalletReservation, UUID> {
}

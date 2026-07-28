package com.poorbet.accountservice.repository;

import com.poorbet.accountservice.domain.model.WalletReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletReservationRepository extends JpaRepository<WalletReservation, UUID> {
}

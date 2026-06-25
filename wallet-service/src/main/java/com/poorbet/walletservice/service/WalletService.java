package com.poorbet.walletservice.service;

import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletBalanceChangedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletEvents;
import com.poorbet.walletservice.domain.exception.InsufficientFundsException;
import com.poorbet.walletservice.domain.exception.WalletNotFoundException;
import com.poorbet.walletservice.domain.model.ReservationStatus;
import com.poorbet.walletservice.domain.model.Wallet;
import com.poorbet.walletservice.domain.model.WalletReservation;
import com.poorbet.walletservice.repository.WalletRepository;
import com.poorbet.walletservice.repository.WalletReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletReservationRepository walletReservationRepository;
    private final OutboxService outboxService;


    @Transactional
    public void handleUserCreated(UUID userId) {
        log.info("handleUserCreated userId={}", userId);

        if (walletRepository.existsByUserId(userId)) {
            log.info("Wallet already exists for userId={}, skipping", userId);
            return;
        }

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(100))
                .build();

        walletRepository.save(wallet);

        outboxService.saveEvent(
                WalletEvents.WALLET_CREATED,
                new WalletCreatedEvent(userId)
        );
    }

    @Transactional
    public void handleCouponWon(CouponWonEvent event) {
        WalletReservation reservation = walletReservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.COMMITTED) return;

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalStateException("Reservation already processed");
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(event.userId())
                .orElseThrow(() -> new IllegalStateException("Wallet not found: " + event.userId()));

        wallet.setBalance(wallet.getBalance().add(event.amount()));

        reservation.setStatus(ReservationStatus.COMMITTED);

        outboxService.saveEvent(
                WalletEvents.WALLET_BALANCE_CHANGED,
                new WalletBalanceChangedEvent(wallet.getUserId(), wallet.getBalance())
        );
    }

    @Transactional(readOnly = true)
    public Wallet getWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user: " + userId));
    }

    @Transactional
    public Wallet debit(UUID userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet updatedWallet = walletRepository.save(wallet);

        outboxService.saveEvent(
                WalletEvents.WALLET_BALANCE_CHANGED,
                new WalletBalanceChangedEvent(updatedWallet.getUserId(), updatedWallet.getBalance())
        );

        return updatedWallet;
    }

    @Transactional
    public void reserve(UUID userId, BigDecimal amount, UUID reservationId) {

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        WalletReservation walletReservation = WalletReservation.create(reservationId, userId, amount);

        walletReservationRepository.save(
                walletReservation
        );

        outboxService.saveEvent(
                WalletEvents.WALLET_BALANCE_CHANGED,
                new WalletBalanceChangedEvent(wallet.getUserId(), wallet.getBalance())
        );
    }
}

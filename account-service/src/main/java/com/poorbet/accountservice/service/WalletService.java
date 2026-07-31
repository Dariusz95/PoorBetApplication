package com.poorbet.accountservice.service;

import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletBalanceChangedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletEvents;
import com.poorbet.accountservice.dto.WalletResponse;
import com.poorbet.accountservice.domain.exception.InsufficientFundsException;
import com.poorbet.accountservice.domain.exception.WalletNotFoundException;
import com.poorbet.accountservice.domain.model.AccountProgress;
import com.poorbet.accountservice.domain.model.LevelConfig;
import com.poorbet.accountservice.domain.model.ReservationStatus;
import com.poorbet.accountservice.domain.model.Wallet;
import com.poorbet.accountservice.domain.model.WalletReservation;
import com.poorbet.accountservice.repository.AccountProgressRepository;
import com.poorbet.accountservice.repository.WalletRepository;
import com.poorbet.accountservice.repository.WalletReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletReservationRepository walletReservationRepository;
    private final AccountProgressRepository accountProgressRepository;
    private final LevelConfigService levelConfigService;
    private final OutboxService outboxService;


    @Transactional
    public void handleUserCreated(UUID userId) {
        log.info("handleUserCreated userId={}", userId);

        if (walletRepository.existsByUserId(userId)) {
            log.info("Wallet already exists for userId={}, skipping", userId);
            return;
        }

        createWallet(userId);
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

        long currentExp = accountProgressRepository.findById(event.userId())
                .map(AccountProgress::getCurrentExp)
                .orElse(0L);
        LevelConfig levelConfig = levelConfigService.findByCurrentExp(currentExp);

        BigDecimal bonusMultiplier = BigDecimal.ONE
                .add(BigDecimal.valueOf(levelConfig.getWinBonusPercent(), 2));
        BigDecimal finalAmount = event.amount().multiply(bonusMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        wallet.setBalance(wallet.getBalance().add(finalAmount));

        reservation.setStatus(ReservationStatus.COMMITTED);

        outboxService.saveEvent(
                WalletEvents.WALLET_BALANCE_CHANGED,
                new WalletBalanceChangedEvent(wallet.getUserId(), wallet.getBalance())
        );
    }

    @Transactional
    public WalletResponse getWallet(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.warn("Wallet missing for userId={} on /me lookup — creating it lazily " +
                            "as a self-healing fallback for a lost USER_CREATED event", userId);
                    return createWallet(userId);
                });

        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }

    private Wallet createWallet(UUID userId) {
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(100))
                .build();

        Wallet saved = walletRepository.save(wallet);

        outboxService.saveEvent(
                WalletEvents.WALLET_CREATED,
                new WalletCreatedEvent(userId)
        );

        return saved;
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

package com.poorbet.walletservice.service;

import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletBalanceChangedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import com.poorbet.walletservice.domain.exception.InsufficientFundsException;
import com.poorbet.walletservice.domain.exception.WalletNotFoundException;
import com.poorbet.walletservice.domain.model.ReservationStatus;
import com.poorbet.walletservice.domain.model.Wallet;
import com.poorbet.walletservice.domain.model.WalletReservation;
import com.poorbet.walletservice.repository.WalletRepository;
import com.poorbet.walletservice.repository.WalletReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WalletService Unit Tests")
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletReservationRepository walletReservationRepository;
    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private WalletService walletService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    private Wallet walletWithBalance(BigDecimal balance) {
        return Wallet.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .balance(balance)
                .build();
    }

    // ==================== handleUserCreated ====================

    @Test
    @DisplayName("Should create wallet with starting balance of 100 when none exists")
    void shouldCreateWalletWithStartingBalance() {
        // Arrange
        when(walletRepository.existsByUserId(userId)).thenReturn(false);

        // Act
        walletService.handleUserCreated(userId);

        // Assert
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getBalance()).isEqualByComparingTo("100");

        ArgumentCaptor<WalletCreatedEvent> eventCaptor = ArgumentCaptor.forClass(WalletCreatedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should skip wallet creation when wallet already exists for user")
    void shouldSkipWalletCreationWhenAlreadyExists() {
        // Arrange
        when(walletRepository.existsByUserId(userId)).thenReturn(true);

        // Act
        walletService.handleUserCreated(userId);

        // Assert
        verify(walletRepository, never()).save(any());
        verifyNoInteractions(outboxService);
    }

    // ==================== getWallet ====================

    @Test
    @DisplayName("Should return wallet when found for user")
    void shouldReturnWalletWhenFound() {
        // Arrange
        Wallet wallet = walletWithBalance(BigDecimal.TEN);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // Act
        Wallet result = walletService.getWallet(userId);

        // Assert
        assertThat(result).isSameAs(wallet);
    }

    @Test
    @DisplayName("Should throw when wallet not found for user")
    void shouldThrowWhenWalletNotFoundOnGet() {
        // Arrange
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> walletService.getWallet(userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(userId.toString());
    }

    // ==================== debit ====================

    @Test
    @DisplayName("Should debit wallet and publish balance changed event")
    void shouldDebitWalletSuccessfully() {
        // Arrange
        Wallet wallet = walletWithBalance(new BigDecimal("100.00"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Act
        Wallet result = walletService.debit(userId, new BigDecimal("40.00"));

        // Assert
        assertThat(result.getBalance()).isEqualByComparingTo("60.00");

        ArgumentCaptor<WalletBalanceChangedEvent> eventCaptor = ArgumentCaptor.forClass(WalletBalanceChangedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().userId()).isEqualTo(userId);
        assertThat(eventCaptor.getValue().balance()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when debit amount exceeds balance")
    void shouldThrowWhenDebitExceedsBalance() {
        // Arrange
        Wallet wallet = walletWithBalance(new BigDecimal("10.00"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThatThrownBy(() -> walletService.debit(userId, new BigDecimal("40.00")))
                .isInstanceOf(InsufficientFundsException.class);

        verify(walletRepository, never()).save(any());
        verifyNoInteractions(outboxService);
    }

    @Test
    @DisplayName("Should throw when debiting a wallet that does not exist")
    void shouldThrowWhenDebitingMissingWallet() {
        // Arrange
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> walletService.debit(userId, BigDecimal.TEN))
                .isInstanceOf(IllegalStateException.class);
    }

    // ==================== reserve ====================

    @Test
    @DisplayName("Should reserve funds, create reservation and publish balance changed event")
    void shouldReserveFundsSuccessfully() {
        // Arrange
        Wallet wallet = walletWithBalance(new BigDecimal("100.00"));
        UUID reservationId = UUID.randomUUID();
        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(wallet));

        // Act
        walletService.reserve(userId, new BigDecimal("30.00"), reservationId);

        // Assert
        assertThat(wallet.getBalance()).isEqualByComparingTo("70.00");

        ArgumentCaptor<WalletReservation> reservationCaptor = ArgumentCaptor.forClass(WalletReservation.class);
        verify(walletReservationRepository).save(reservationCaptor.capture());
        assertThat(reservationCaptor.getValue().getId()).isEqualTo(reservationId);
        assertThat(reservationCaptor.getValue().getUserId()).isEqualTo(userId);
        assertThat(reservationCaptor.getValue().getAmount()).isEqualByComparingTo("30.00");
        assertThat(reservationCaptor.getValue().getStatus()).isEqualTo(ReservationStatus.RESERVED);

        ArgumentCaptor<WalletBalanceChangedEvent> eventCaptor = ArgumentCaptor.forClass(WalletBalanceChangedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().balance()).isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when reserve amount exceeds balance")
    void shouldThrowWhenReserveExceedsBalance() {
        // Arrange
        Wallet wallet = walletWithBalance(new BigDecimal("10.00"));
        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThatThrownBy(() -> walletService.reserve(userId, new BigDecimal("30.00"), UUID.randomUUID()))
                .isInstanceOf(InsufficientFundsException.class);

        verifyNoInteractions(walletReservationRepository);
        verifyNoInteractions(outboxService);
    }

    @Test
    @DisplayName("Should throw WalletNotFoundException when reserving for missing wallet")
    void shouldThrowWhenReservingMissingWallet() {
        // Arrange
        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> walletService.reserve(userId, BigDecimal.TEN, UUID.randomUUID()))
                .isInstanceOf(WalletNotFoundException.class);
    }

    // ==================== handleCouponWon ====================

    private WalletReservation reservationWithStatus(UUID reservationId, ReservationStatus status) {
        WalletReservation reservation = WalletReservation.create(reservationId, userId, new BigDecimal("20.00"));
        reservation.setStatus(status);
        return reservation;
    }

    @Test
    @DisplayName("Should credit wallet and commit reservation when coupon is won")
    void shouldCreditWalletWhenCouponWon() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        WalletReservation reservation = reservationWithStatus(reservationId, ReservationStatus.RESERVED);
        Wallet wallet = walletWithBalance(new BigDecimal("50.00"));
        CouponWonEvent event = new CouponWonEvent(UUID.randomUUID(), reservationId, userId, new BigDecimal("20.00"));

        when(walletReservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(wallet));

        // Act
        walletService.handleCouponWon(event);

        // Assert
        assertThat(wallet.getBalance()).isEqualByComparingTo("70.00");
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMMITTED);

        ArgumentCaptor<WalletBalanceChangedEvent> eventCaptor = ArgumentCaptor.forClass(WalletBalanceChangedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().balance()).isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("Should be idempotent when reservation is already committed")
    void shouldBeIdempotentWhenAlreadyCommitted() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        WalletReservation reservation = reservationWithStatus(reservationId, ReservationStatus.COMMITTED);
        CouponWonEvent event = new CouponWonEvent(UUID.randomUUID(), reservationId, userId, new BigDecimal("20.00"));

        when(walletReservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Act
        walletService.handleCouponWon(event);

        // Assert
        verifyNoInteractions(walletRepository);
        verifyNoInteractions(outboxService);
    }

    @Test
    @DisplayName("Should throw when reservation is in an unexpected state (e.g. RELEASED)")
    void shouldThrowWhenReservationAlreadyProcessed() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        WalletReservation reservation = reservationWithStatus(reservationId, ReservationStatus.RELEASED);
        CouponWonEvent event = new CouponWonEvent(UUID.randomUUID(), reservationId, userId, new BigDecimal("20.00"));

        when(walletReservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> walletService.handleCouponWon(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already processed");

        verifyNoInteractions(walletRepository);
        verifyNoInteractions(outboxService);
    }

    @Test
    @DisplayName("Should throw when reservation referenced by coupon-won event is not found")
    void shouldThrowWhenReservationNotFound() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        CouponWonEvent event = new CouponWonEvent(UUID.randomUUID(), reservationId, userId, new BigDecimal("20.00"));

        when(walletReservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> walletService.handleCouponWon(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Reservation not found");
    }

    @Test
    @DisplayName("Should throw when wallet referenced by reservation is not found")
    void shouldThrowWhenWalletNotFoundOnCouponWon() {
        // Arrange
        UUID reservationId = UUID.randomUUID();
        WalletReservation reservation = reservationWithStatus(reservationId, ReservationStatus.RESERVED);
        CouponWonEvent event = new CouponWonEvent(UUID.randomUUID(), reservationId, userId, new BigDecimal("20.00"));

        when(walletReservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> walletService.handleCouponWon(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Wallet not found");

        verifyNoInteractions(outboxService);
    }
}

package com.poorbet.accountservice.service;

import com.poorbet.accountservice.repository.ProcessedEventRepository;
import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponWonListener Unit Tests")
class CouponWonListenerTest {

    @Mock
    private WalletService walletService;

    @Mock
    private ProgressService progressService;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private CouponWonListener couponWonListener;

    @Test
    @DisplayName("Should grant EXP for the stake before crediting the wallet, so the win's own EXP counts toward its bonus level")
    void shouldDelegateToWalletService() {
        // Arrange
        CouponWonEvent payload = new CouponWonEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("25.00"), new BigDecimal("10.00")
        );
        EventEnvelope<CouponWonEvent> envelope = new EventEnvelope<>(UUID.randomUUID(), "COUPON_WON", "v1", "coupon-service", payload);
        when(processedEventRepository.existsById(envelope.eventId())).thenReturn(false);

        // Act
        couponWonListener.handleCouponWon(envelope);

        // Assert
        InOrder inOrder = inOrder(progressService, walletService);
        inOrder.verify(progressService).addExpForStake(payload.userId(), payload.stake());
        inOrder.verify(walletService).handleCouponWon(payload);
        verify(processedEventRepository).save(any());
    }

    @Test
    @DisplayName("Should skip processing when the event was already processed")
    void shouldSkipWhenEventAlreadyProcessed() {
        // Arrange
        CouponWonEvent payload = new CouponWonEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("25.00"), new BigDecimal("10.00")
        );
        EventEnvelope<CouponWonEvent> envelope = new EventEnvelope<>(UUID.randomUUID(), "COUPON_WON", "v1", "coupon-service", payload);
        when(processedEventRepository.existsById(envelope.eventId())).thenReturn(true);

        // Act
        couponWonListener.handleCouponWon(envelope);

        // Assert
        verify(progressService, never()).addExpForStake(any(), any());
        verify(walletService, never()).handleCouponWon(any());
    }
}

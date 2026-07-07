package com.poorbet.walletservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponWonListener Unit Tests")
class CouponWonListenerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private CouponWonListener couponWonListener;

    @Test
    @DisplayName("Should delegate coupon-won payload to WalletService")
    void shouldDelegateToWalletService() {
        // Arrange
        CouponWonEvent payload = new CouponWonEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("25.00")
        );
        EventEnvelope<CouponWonEvent> envelope = new EventEnvelope<>("COUPON_WON", "v1", "coupon-service", payload);

        // Act
        couponWonListener.handleUserCreated(envelope);

        // Assert
        verify(walletService).handleCouponWon(payload);
    }
}

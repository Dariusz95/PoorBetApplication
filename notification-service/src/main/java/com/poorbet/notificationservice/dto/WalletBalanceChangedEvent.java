package com.poorbet.notificationservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceChangedEvent(
        UUID userId,
        BigDecimal balance
) {
}

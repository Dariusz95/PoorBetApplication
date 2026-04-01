package com.poorbet.walletservice.event;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceChangedEvent(
        UUID userId,
        BigDecimal balance
) {
}

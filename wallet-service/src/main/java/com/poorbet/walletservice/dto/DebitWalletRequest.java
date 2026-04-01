package com.poorbet.walletservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DebitWalletRequest(
        @NotNull
        @DecimalMin("1.00")
        BigDecimal amount
) {
}

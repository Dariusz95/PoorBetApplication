package com.poorbet.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreditWalletRequest(
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount
) {
}

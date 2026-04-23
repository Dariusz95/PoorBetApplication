package com.poorbet.couponservice.dto;

import java.math.BigDecimal;

public record DebitWalletRequest(
        BigDecimal amount
) {
}

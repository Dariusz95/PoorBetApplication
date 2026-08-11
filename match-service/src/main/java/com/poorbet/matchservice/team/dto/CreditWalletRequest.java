package com.poorbet.matchservice.team.dto;

import java.math.BigDecimal;

public record CreditWalletRequest(
        BigDecimal amount
) {
}

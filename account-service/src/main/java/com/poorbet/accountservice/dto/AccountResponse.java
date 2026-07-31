package com.poorbet.accountservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID userId,
        BigDecimal balance,
        int level,
        long currentExp,
        Long requiredExpForNextLevel,
        int winBonusPercent
) {
}

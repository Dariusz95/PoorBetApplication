package com.poorbet.accountservice.dto;

public record AccountProgressResponse(
        int level,
        long currentExp,
        Long requiredExpForNextLevel,
        int winBonusPercent
) {
}

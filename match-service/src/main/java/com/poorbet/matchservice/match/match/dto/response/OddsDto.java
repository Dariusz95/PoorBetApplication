package com.poorbet.matchservice.match.match.dto.response;


import java.math.BigDecimal;
import java.util.UUID;

public record OddsDto(
        UUID id,
        BigDecimal homeWin,
        BigDecimal draw,
        BigDecimal awayWin
        ) {
}
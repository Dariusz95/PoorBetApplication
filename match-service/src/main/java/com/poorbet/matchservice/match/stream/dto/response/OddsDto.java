package com.poorbet.matchservice.match.stream.dto.response;


import java.util.UUID;

public record OddsDto(
        UUID id,
        double homeWin,
        double draw,
        double awayWin
        ) {
}
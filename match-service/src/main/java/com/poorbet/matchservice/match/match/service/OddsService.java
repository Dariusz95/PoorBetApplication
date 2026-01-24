package com.poorbet.matchservice.match.match.service;

import com.poorbet.matchservice.match.match.domain.OddsType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface OddsService {
    public Optional<BigDecimal> getOdds(UUID matchId, OddsType type);
}

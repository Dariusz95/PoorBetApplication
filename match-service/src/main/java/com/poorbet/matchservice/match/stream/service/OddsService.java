package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.enums.OddsType;

import java.util.Optional;
import java.util.UUID;

public interface OddsService {
    public Optional<Double> getOdds(UUID matchId, OddsType type);
}

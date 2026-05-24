package com.poorbet.odds_engine_service.oddstraining.generator.config;


import com.poorbet.odds_engine_service.oddstraining.team.TeamTier;

import java.util.List;

public record TierPairConfig(TeamTier tier1, TeamTier tier2, boolean allowSameTeam) {

    public static final List<TierPairConfig> DEFAULT_CONFIGS = List.of(
            new TierPairConfig(TeamTier.WEAK, TeamTier.WEAK, false),
            new TierPairConfig(TeamTier.AVERAGE, TeamTier.AVERAGE, false),
            new TierPairConfig(TeamTier.STRONG, TeamTier.STRONG, false),
            new TierPairConfig(TeamTier.WEAK, TeamTier.AVERAGE, true),
            new TierPairConfig(TeamTier.WEAK, TeamTier.STRONG, true),
            new TierPairConfig(TeamTier.AVERAGE, TeamTier.STRONG, true)
    );
}

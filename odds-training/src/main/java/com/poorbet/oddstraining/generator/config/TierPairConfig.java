package com.poorbet.oddstraining.generator.config;


import com.poorbet.oddstraining.domain.team.TeamTier;

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

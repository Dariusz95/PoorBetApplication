package com.poorbet.oddstraining.domain.team;

import java.util.Map;

public final class TeamPowerConfig {

    private TeamPowerConfig() {}

    public static final Map<TeamTier, PowerRange> ATTACK = Map.of(
            TeamTier.WEAK, new PowerRange(30, 50),
            TeamTier.AVERAGE, new PowerRange(50, 70),
            TeamTier.STRONG, new PowerRange(70, 100)
    );

    public static final Map<TeamTier, PowerRange> DEFENCE = Map.of(
            TeamTier.WEAK, new PowerRange(30, 50),
            TeamTier.AVERAGE, new PowerRange(50, 70),
            TeamTier.STRONG, new PowerRange(70, 100)
    );
}


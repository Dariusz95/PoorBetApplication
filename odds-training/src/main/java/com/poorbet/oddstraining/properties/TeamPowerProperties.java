package com.poorbet.oddstraining.properties;

import com.poorbet.oddstraining.domain.team.TeamTier;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.EnumMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "team-power")
public class TeamPowerProperties {

    @NotNull
    private Map<TeamTier, TierPower> tiers = new EnumMap<>(TeamTier.class);

    public Map<TeamTier, TierPower> getTiers() {
        return tiers;
    }

    public void setTiers(Map<TeamTier, TierPower> tiers) {
        this.tiers = tiers;
    }

    public record TierPower(
            @NotNull Range attack,
            @NotNull Range defence
    ) {}

    public record Range(
            double min,
            double max
    ) {}
}
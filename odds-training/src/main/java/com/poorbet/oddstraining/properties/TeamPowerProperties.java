package com.poorbet.oddstraining.properties;

import com.poorbet.oddstraining.domain.team.TeamTier;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.EnumMap;
import java.util.Map;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "team-power")
public class TeamPowerProperties {

    @NotNull
    private Map<TeamTier, TierPower> tiers = new EnumMap<>(TeamTier.class);

    public record TierPower(
            @NotNull Range attack,
            @NotNull Range defence
    ) {
    }

    public record Range(
            double min,
            double max
    ) {
    }
}
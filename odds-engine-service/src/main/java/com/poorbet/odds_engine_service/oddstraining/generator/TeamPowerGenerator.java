package com.poorbet.odds_engine_service.oddstraining.generator;

import com.poorbet.odds_engine_service.oddstraining.properties.TeamPowerProperties;
import com.poorbet.odds_engine_service.oddstraining.team.TeamTier;
import com.poorbet.odds_engine_service.simulation.model.TeamPower;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TeamPowerGenerator {

    private final TeamPowerProperties properties;
    private final Random random;

    public List<TeamPower> generateForTier(TeamTier tier, int count) {
        TeamPowerProperties.TierPower power = properties.getTiers().get(tier);

        return IntStream.range(0, count)
                .mapToObj(i -> new TeamPower(
                        randomBetween(power.attack()),
                        randomBetween(power.defence())
                ))
                .toList();
    }

    int randomBetween(TeamPowerProperties.Range range) {
        double value = range.min() + random.nextDouble() * (range.max() - range.min());
        return (int) Math.round(value);
    }
}

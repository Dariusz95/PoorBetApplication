package com.poorbet.oddstraining.generator;

import com.poorbet.oddstraining.domain.team.TeamTier;
import com.poorbet.oddstraining.model.TeamPower;
import com.poorbet.oddstraining.properties.TeamPowerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TeamGenerator {

    private final TeamPowerProperties properties;
    private final Random random = new Random();

    public List<TeamPower> generate(int countPerTier){
        return Arrays.stream(TeamTier.values())
                .flatMap(tier->generateForTier(tier, countPerTier).stream())
                .toList();
    }

    private List<TeamPower> generateForTier(TeamTier tier, int count){
        TeamPowerProperties.TierPower power = properties.getTiers().get(tier);

        return IntStream.range(0, count)
                .mapToObj(i-> new TeamPower(
                        randomBetween(power.attack()),
                        randomBetween(power.defence())
                ))
                .toList();
    }

    private double randomBetween(TeamPowerProperties.Range range) {
        return range.min() + random.nextDouble() * (range.max() - range.min());
    }
}

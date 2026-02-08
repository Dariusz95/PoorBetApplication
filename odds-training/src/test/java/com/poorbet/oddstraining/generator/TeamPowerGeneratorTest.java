package com.poorbet.oddstraining.generator;

import com.poorbet.oddstraining.domain.team.TeamTier;
import com.poorbet.oddstraining.model.TeamPower;
import com.poorbet.oddstraining.properties.TeamPowerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TeamPowerGeneratorTest {

    @Mock
    private TeamPowerProperties properties;
    @Mock
    private Random random;
    @InjectMocks
    private TeamPowerGenerator generator;

    private TeamPowerProperties.TierPower tierPower() {
        return new TeamPowerProperties.TierPower(
                new TeamPowerProperties.Range(70, 100),
                new TeamPowerProperties.Range(70, 100)
        );
    }

    @BeforeEach
    void setup() {
        Map<TeamTier, TeamPowerProperties.TierPower> tiers = new EnumMap<>(TeamTier.class);
        tiers.put(TeamTier.STRONG, tierPower());

        when(properties.getTiers()).thenReturn(tiers);
    }

    @Test
    void shouldGenerateGivenNumberOfTeamPowers() {
        //given
        when(random.nextDouble()).thenReturn(0.5);

        //when
        List<TeamPower> result = generator.generateForTier(TeamTier.STRONG, 3);

        //then
        assertThat(result).hasSize(3);
    }


    @Test
    void generatedValuesShouldBeWithinConfiguredRange() {
        //given
        when(random.nextDouble()).thenReturn(0.0, 1.0);

        //when
        List<TeamPower> result = generator.generateForTier(TeamTier.STRONG, 1);

        //then
        TeamPower firstResult = result.getFirst();
        assertThat(firstResult.attackPower()).isBetween(70, 100);
        assertThat(firstResult.defencePower()).isBetween(70, 100);
    }
}
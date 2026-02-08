package com.poorbet.oddstraining.generator;

import com.poorbet.oddstraining.domain.team.TeamTier;
import com.poorbet.oddstraining.model.TeamPower;
import com.poorbet.oddstraining.properties.TrainingProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchGeneratorTest {

    @Mock
    TeamPowerGenerator teamPowerGenerator;

    @Mock
    TrainingProperties trainingProperties;

    @InjectMocks
    MatchGenerator matchGenerator;


    private TeamPower team() {
        return new TeamPower(50, 50);
    }

    private List<TeamPower> teams(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> team())
                .toList();
    }

    @Test
    void shouldGenerateTeamsForAllTiers() {
        // given
        when(trainingProperties.countPerTier()).thenReturn(2);
        when(trainingProperties.repetitions()).thenReturn(1);

        when(teamPowerGenerator.generateForTier(any(), eq(2)))
                .thenReturn(teams(2));

        // when
        matchGenerator.getMatches();

        // then
        verify(teamPowerGenerator).generateForTier(TeamTier.WEAK, 2);
        verify(teamPowerGenerator).generateForTier(TeamTier.AVERAGE, 2);
        verify(teamPowerGenerator).generateForTier(TeamTier.STRONG, 2);
    }

}
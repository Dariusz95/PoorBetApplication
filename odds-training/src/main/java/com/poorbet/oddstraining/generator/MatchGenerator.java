package com.poorbet.oddstraining.generator;

import com.poorbet.oddstraining.domain.team.TeamTier;
import com.poorbet.oddstraining.generator.config.TierPairConfig;
import com.poorbet.oddstraining.model.TeamPower;
import com.poorbet.oddstraining.properties.TrainingProperties;
import com.poorbet.oddstraining.request.SimulationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MatchGenerator {
    private final TeamPowerGenerator teamPowerGenerator;
    private final TrainingProperties trainingProperties;

    public List<SimulationRequestDto> getMatches() {
        int countPerTier = trainingProperties.countPerTier();

        List<TeamPower> weakTeams = teamPowerGenerator.generateForTier(TeamTier.WEAK, countPerTier);
        List<TeamPower> averageTeams = teamPowerGenerator.generateForTier(TeamTier.AVERAGE, countPerTier);
        List<TeamPower> strongTeams = teamPowerGenerator.generateForTier(TeamTier.STRONG, countPerTier);

        Map<TeamTier, List<TeamPower>> teamsByTier = Map.of(
                TeamTier.WEAK, weakTeams,
                TeamTier.AVERAGE, averageTeams,
                TeamTier.STRONG, strongTeams
        );

        List<SimulationRequestDto> allPairs = TierPairConfig.DEFAULT_CONFIGS.stream()
                .flatMap(config -> {
                    List<TeamPower> list1 = teamsByTier.get(config.tier1());
                    List<TeamPower> list2 = teamsByTier.get(config.tier2());
                    return createPairs(list1, list2, config.allowSameTeam()).stream();
                })
                .toList();


        return allPairs;
    }


    private List<SimulationRequestDto> createPairs(List<TeamPower> list1, List<TeamPower> list2, boolean includeSameTeam) {
        List<SimulationRequestDto> pairs = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (!includeSameTeam && list1 == list2 && i == j) {
                    continue;
                }

                for (int r = 0; r < trainingProperties.repetitions(); r++) {
                    pairs.add(new SimulationRequestDto(
                            UUID.randomUUID(),
                            list1.get(i),
                            list2.get(j)
                    ));
                }
            }
        }
        return pairs;
    }
}

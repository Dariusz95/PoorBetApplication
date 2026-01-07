package com.poorbet.oddstraining.generator;

import com.poorbet.oddstraining.domain.team.TeamTier;
import com.poorbet.oddstraining.generator.config.TierPairConfig;
import com.poorbet.oddstraining.model.Match;
import com.poorbet.oddstraining.model.TeamPower;
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
    private final TeamGenerator teamGenerator;

    public List<SimulationRequestDto> getMatches(){
        List<TeamPower> weakTeams = teamGenerator.generateForTier(TeamTier.WEAK, 5);
        List<TeamPower> averageTeams = teamGenerator.generateForTier(TeamTier.AVERAGE, 5);
        List<TeamPower> strongTeams = teamGenerator.generateForTier(TeamTier.STRONG, 5);

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
                pairs.add(new SimulationRequestDto(UUID.randomUUID(), list1.get(i), list2.get(j)));
            }
        }
        return pairs;
    }
}

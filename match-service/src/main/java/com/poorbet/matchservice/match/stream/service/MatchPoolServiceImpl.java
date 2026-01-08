package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.client.TeamsClient;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolServiceImpl implements MatchPoolService{
    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolSimulationService matchPoolSimulationService;
//    private final TeamsClient teamsClient;

    @Transactional
    public void startPool(UUID poolId) {
        MatchPool pool = matchPoolRepository.findById(poolId)
                .orElseThrow();

        if (pool.getStatus() != PoolStatus.BETTABLE) return;

//        List<UUID> teamIds = pool.getMatches().stream()
//                        .flatMap(match -> Stream.of(match.getHomeTeamId(), match.getAwayTeamId()))
//                        .distinct()
//                        .toList();

//        List<TeamStatsDto> teamStats = teamsClient.getStatsByIds(teamIds);

        pool.setStatus(PoolStatus.STARTED);
        pool.getMatches().forEach(m -> m.setStatus(MatchStatus.LIVE));

        matchPoolRepository.save(pool);

        matchPoolSimulationService.startPoolSimulation(pool.getId());
    }
}

package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.client.TeamsClient;
import com.poorbet.matchservice.match.stream.config.MatchPoolProperties;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.model.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolService {
    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolSimulationService matchPoolSimulationService;
    private final TeamsClient teamsClient;

    @Transactional
    public void startPool(UUID poolId) {
        MatchPool pool = matchPoolRepository.findById(poolId)
                .orElseThrow();

        if (pool.getStatus() != PoolStatus.BETTABLE) return;

        List<UUID> teamIds = pool.getMatches().stream()
                        .flatMap(match -> Stream.of(match.getHomeTeamId(), match.getAwayTeamId()))
                        .distinct()
                        .toList();

        List<TeamStatsDto> teamStats = teamsClient.getStatsByIds(teamIds);

        pool.setStatus(PoolStatus.LIVE);
        pool.getMatches().forEach(m -> m.setStatus(MatchStatus.LIVE));

        matchPoolRepository.save(pool);

        matchPoolSimulationService.startPoolSimulation(pool.getId(), teamStats);
    }
}

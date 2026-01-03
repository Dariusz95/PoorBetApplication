package com.poorbet.matchservice.match.stream.scheduler;

import com.poorbet.matchservice.match.stream.client.TeamsClient;
import com.poorbet.matchservice.match.stream.config.MatchPoolProperties;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.model.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import com.poorbet.matchservice.match.stream.service.MatchPoolSimulationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolScheduler {

    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolSimulationService simulationService;
    private final TaskScheduler taskScheduler;
    private final MatchPoolProperties properties;
    private final TeamsClient teamsClient;

    @PostConstruct
    public void initialize() {
        scheduleMissingPools();
    }

    private void scheduleMissingPools() {
        int matchesPerPool = properties.getMatchesPerPool();
        int poolsInAdvance = properties.getPoolsInAdvance();
        int intervalMinutes = properties.getPoolIntervalMinutes();

        OffsetDateTime now = OffsetDateTime.now();

        List<MatchPool> futurePools = matchPoolRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PoolStatus.BETTABLE
                        && p.getScheduledStartTime().isAfter(now))
                .sorted(Comparator.comparing(MatchPool::getScheduledStartTime))
                .toList();

        OffsetDateTime nextStartTime = now.plusMinutes(intervalMinutes);
        if (!futurePools.isEmpty()) {
            OffsetDateTime lastTime = futurePools.get(futurePools.size() - 1).getScheduledStartTime();
            if (lastTime.isAfter(nextStartTime)) {
                nextStartTime = lastTime.plusMinutes(intervalMinutes);
            }
        }

        log.info("📊 Pule w kolejce: {}/{}", futurePools.size(), poolsInAdvance);

        int missingPools = poolsInAdvance - futurePools.size();

        if (missingPools <= 0) {
            return;
        }

        for (int i = 0; i < missingPools; i++) {
            MatchPool pool = MatchPool.builder()
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(nextStartTime)
                    .build();

            List<TeamStatsDto> teams = teamsClient.randomTeams();

            createMatches(teams, pool)
                    .forEach(pool::addMatch);

            matchPoolRepository.save(pool);

            schedulePool(pool);

            nextStartTime = nextStartTime.plusMinutes(intervalMinutes);
        }
    }
    private void schedulePool(MatchPool pool) {
        taskScheduler.schedule(
                () -> startPool(pool.getId()),
                pool.getScheduledStartTime().toInstant()
        );
        log.info("⏱ Zaplanowano pulę {} na {}", pool.getId(), pool.getScheduledStartTime());
    }

    @Transactional
    public void startPool(UUID poolId) {
        MatchPool pool = matchPoolRepository.findById(poolId)
                .orElseThrow();

        if (pool.getStatus() != PoolStatus.BETTABLE) return;

        pool.setStatus(PoolStatus.LIVE);
        pool.getMatches().forEach(m -> m.setStatus(MatchStatus.LIVE));
        matchPoolRepository.save(pool);

        simulationService.startPoolSimulation(pool);

        scheduleMissingPools();
    }

    public List<Match> createMatches(List<TeamStatsDto> teams, MatchPool pool) {

        if (teams.size() % 2 != 0) {
            throw new IllegalArgumentException("Number of teams must be even");
        }

        Collections.shuffle(teams);

        List<Match> matches = new ArrayList<>();

        for (int i = 0; i < teams.size(); i += 2) {
            TeamStatsDto home = teams.get(i);
            TeamStatsDto away = teams.get(i + 1);

            Match match = Match.builder()
                    .pool(pool)
                    .homeTeamId(home.getId())
                    .awayTeamId(away.getId())
                    .homeTeam(home)
                    .awayTeam(away)
                    .homeGoals(0)
                    .awayGoals(0)
                    .currentMinute(0)
                    .status(MatchStatus.SCHEDULED)
                    .build();

            pool.addMatch(match);
        }

        return matches;
    }
}

package com.poorbet.matchservice.match.stream.scheduler;

import com.poorbet.matchservice.match.stream.config.MatchPoolProperties;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.model.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import com.poorbet.matchservice.match.stream.service.MatchPoolSimulationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolScheduler {

    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolSimulationService simulationService;
    private final TaskScheduler taskScheduler;
    private final MatchPoolProperties properties;

    @PostConstruct
    public void initialize() {
        scheduleMissingPools();
    }

    private void scheduleMissingPools() {
        int matchesPerPool = properties.getMatchesPerPool();
        int poolsInAdvance = properties.getPoolsInAdvance();
        int intervalMinutes = properties.getPoolIntervalMinutes();

        LocalDateTime now = LocalDateTime.now();

        List<MatchPool> futurePools = matchPoolRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PoolStatus.BETTABLE
                        && p.getScheduledStartTime().isAfter(now))
                .sorted(Comparator.comparing(MatchPool::getScheduledStartTime))
                .toList();

        LocalDateTime nextStartTime = now.plusMinutes(intervalMinutes);
        if (!futurePools.isEmpty()) {
            LocalDateTime lastTime = futurePools.get(futurePools.size() - 1).getScheduledStartTime();
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
//            PoolStatus status;
//            if (i == 0 && futurePools.isEmpty()) status = PoolStatus.BETTABLE;
//            else if (i < missingPools - 1) status = PoolStatus.BETTABLE;
//            else status = PoolStatus.HIDDEN;

//            MatchPool pool = MatchPool.builder()
//                    .status(PoolStatus.BETTABLE)
//                    .scheduledStartTime(nextStartTime)
//                    .matches(generateMatches(matchesPerPool))
//                    .build();
//
//            try {
//                matchPoolRepository.save(pool);
//            } catch (Exception e) {
//                log.error("❌ Błąd przy save: {}", e);
//            }

            log.info("📊 nextStartTime -> {}", nextStartTime);

            MatchPool pool = MatchPool.builder()
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(nextStartTime)
                    .build();

            generateMatches(pool, matchesPerPool)
                    .forEach(pool::addMatch);

            matchPoolRepository.save(pool);

            try {
                schedulePool(pool);
                log.error("❌ ❌❌❌❌❌schedulePool");
            } catch (Exception e) {
                log.error("❌ Błąd przy planowaniu puli: {}", pool.getId(), e);
            }

            nextStartTime = nextStartTime.plusMinutes(intervalMinutes);
        }
    }
    private void schedulePool(MatchPool pool) {
        taskScheduler.schedule(
                () -> startPool(pool.getId()),
                pool.getScheduledStartTime().atZone(ZoneId.systemDefault()).toInstant()
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

        // uruchom symulacje wszystkich meczów
        simulationService.startPoolSimulation(pool);

        // po starcie sprawdzamy, czy trzeba uzupełnić pule w przyszłości
        scheduleMissingPools();
    }

//    private List<Match> generateMatches(int count) {
//        return IntStream.range(0, count)
//                .mapToObj(i -> Match.builder()
//                        .homeTeamId(UUID.randomUUID())
//                        .awayTeamId(UUID.randomUUID())
//                        .homeGoals(0)
//                        .awayGoals(0)
//                        .currentMinute(0)
//                        .status(MatchStatus.SCHEDULED)
//                        .build())
//                .toList();
//    }

    private List<Match> generateMatches(MatchPool pool, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Match.builder()
                        .pool(pool)
                        .homeTeamId(UUID.randomUUID())
                        .awayTeamId(UUID.randomUUID())
                        .homeGoals(0)
                        .awayGoals(0)
                        .currentMinute(0)
                        .status(MatchStatus.SCHEDULED)
                        .build())
                .toList();
    }
}

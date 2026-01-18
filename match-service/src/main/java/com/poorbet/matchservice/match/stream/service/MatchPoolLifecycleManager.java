package com.poorbet.matchservice.match.stream.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class MatchPoolLifecycleManager {

    private final MatchRepository matchRepository;
    private final MatchPoolRepository matchPoolRepository;
    private final LiveMatchSimulationManager liveMatchSimulationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final MatchPoolEventPublisher matchPoolEventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMatchFinished(Match match) {
        UUID poolId = match.getPool().getId();

        log.info("🚀 id - {} - handleMatchFinished", match.getId());

        long remaining = matchRepository.countByPoolIdAndStatusNot(poolId, MatchStatus.FINISHED);

        log.info("🚀 Match id - {} - remaining  {}", match.getId(), remaining);


        if (remaining > 0) {
            return;
        }

        String lockKey = "match-pool:" + poolId + ":finalize";

        Boolean first = redisTemplate.opsForValue()
                .setIfAbsent(
                        lockKey,
                        "1",
                        5,
                        TimeUnit.MINUTES
                );


        if (!Boolean.TRUE.equals(first)) {
            return;
        }

        MatchPool matchPool = matchPoolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalStateException("Match not found: " + poolId));

        log.info("🚀 matchPool  {}", matchPool);


        matchPool.setStatus(PoolStatus.FINISHED);
        matchPoolRepository.save(matchPool);

        liveMatchSimulationManager.notifyPoolFinished(poolId);

        matchPoolEventPublisher.publishPoolFinished(poolId);
    }
}

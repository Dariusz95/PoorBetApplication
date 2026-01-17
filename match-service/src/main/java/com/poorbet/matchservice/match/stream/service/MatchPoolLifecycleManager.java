package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class MatchPoolLifecycleManager {

    private final MatchRepository matchRepository;
    private final MatchPoolRepository matchPoolRepository;
    private final LiveMatchSimulationManager liveMatchSimulationManager;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMatchFinished(Match match) {
        UUID poolId = match.getPool().getId();

        long remaining = matchRepository.countByPoolIdAndStatusNot(poolId, MatchStatus.FINISHED);

        if (remaining > 0) {
            return;
        }

        String lockKey = "match-pool:" + poolId + ":finalize";

        String currentValue = redisTemplate.opsForValue().get(lockKey);
        log.info("🚀 Current Redis value for {}: {}", lockKey, currentValue);

        Boolean first = redisTemplate.opsForValue()
                .setIfAbsent(
                        "match-pool:" + poolId + ":finalize",
                        "1",
                        5,
                        TimeUnit.MINUTES
                );

        log.info("🚀 setIfAbsent result: {}", first);

        if (!Boolean.TRUE.equals(first)) {
            return;
        }

        MatchPool matchPool = matchPoolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalStateException("Match not found: " + poolId));

        log.info("🚀 matchPool changeStatus  {}", matchPool);


        matchPool.setStatus(PoolStatus.FINISHED);
        matchPoolRepository.save(matchPool);

        notifyPoolFinished(poolId);
    }

    private void notifyPoolFinished(UUID poolId) {
        LiveMatchEventDto poolFinishedEvent = LiveMatchEventDto.poolFinished(poolId);

        var sink = liveMatchSimulationManager.getSink();

        sink.tryEmitNext(poolFinishedEvent);
    }
}

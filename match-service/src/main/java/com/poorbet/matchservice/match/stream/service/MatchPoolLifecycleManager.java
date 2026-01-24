package com.poorbet.matchservice.match.stream.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.poorbet.matchservice.match.stream.dto.MatchResultDto;
import jakarta.persistence.EntityNotFoundException;
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

        long remainingLive = matchRepository.countByPoolIdAndStatus(poolId, MatchStatus.LIVE);

        log.info("🚀 Match id - {} - remaining  {}", match.getId(), remainingLive);

        if (remainingLive > 0) {
            return;
        }

        String lockKey = "match-pool:" + poolId + ":finalize";

        Boolean first = redisTemplate.opsForValue()
                .setIfAbsent(
                        lockKey,
                        "1",
                        4,
                        TimeUnit.MINUTES
                );


        if (!Boolean.TRUE.equals(first)) {
            return;
        }

        int updated = matchPoolRepository.updateStatus(poolId, PoolStatus.FINISHED);

        if (updated == 0) {
            throw new EntityNotFoundException("MatchPool not found: " + poolId);
        }

        List<UUID> matchIds = matchPoolRepository.findById(poolId)
                .map(MatchPool::getMatches)
                .orElse(List.of())
                .stream()
                .map(Match::getId)
                .toList();

        sendPoolFinishedEventsAsync(poolId, matchIds);
    }

    private void sendPoolFinishedEventsAsync(UUID poolId, List<MatchResultDto> matchIds) {
        try {
            matchPoolEventPublisher.publishMatchesFinished(matchIds);

            liveMatchSimulationManager.notifyPoolFinished(poolId);

        } catch (Exception e) {
            log.error("Failed to notify about finished pool {}", poolId, e);
        }
    }
}

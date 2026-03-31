package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.match.matchpool.repository.MatchPoolRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        List<MatchResultDto> results = matchPoolRepository.getResults(poolId);

        sendPoolFinishedEventsAsync(poolId, results);
    }

    private void sendPoolFinishedEventsAsync(UUID poolId, List<MatchResultDto> results) {
        try {
            List<MatchResultEventDto> eventResults = results.stream()
                    .map(this::toEventDto)
                    .toList();

            matchPoolEventPublisher.publishMatchesFinished(eventResults);

            liveMatchSimulationManager.notifyPoolFinished(poolId);

        } catch (Exception e) {
            log.error("Failed to notify about finished pool {}", poolId, e);
        }
    }

    public MatchResultEventDto toEventDto(MatchResultDto dto) {
        return new MatchResultEventDto(
                dto.getId(),
                dto.getHomeGoals(),
                dto.getAwayGoals()
        );
    }
}

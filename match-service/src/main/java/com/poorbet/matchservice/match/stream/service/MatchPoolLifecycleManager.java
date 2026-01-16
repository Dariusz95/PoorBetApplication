package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MatchPoolLifecycleManager {

    private final MatchRepository matchRepository;
    private final MatchPoolRepository matchPoolRepository;
    private final LiveMatchSimulationManager liveMatchSimulationManager;

    @Transactional()
    public void handleMatchFinished(Match match) {
        UUID poolId = match.getPool().getId();

        boolean allFinished = matchRepository.findByPoolId(poolId)
                .stream()
                .allMatch(m -> m.getStatus().equals(MatchStatus.FINISHED));

        if (!allFinished) {
            return;
        }

        MatchPool matchPool = matchPoolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalStateException("Match not found: " + poolId));

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

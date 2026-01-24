package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.match.tx.AfterCommitHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.poorbet.matchservice.match.match.domain.MatchStatus.FINISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchFinishServiceImpl implements MatchFinishService {

    private final MatchRepository matchRepository;
    private final MatchPoolLifecycleManager lifecycleManager;
    private final AfterCommitHandler afterCommitHandler;

    @Transactional
    public void finishMatch(LiveMatchEventDto event) {
        log.info("🚀 finishMatch {}", event);

        Match match = matchRepository
                .findById(event.getId())
                .orElseThrow(() -> new IllegalStateException("Match not found: " + event.getId()));

        match.setStatus(FINISHED);
        match.setHomeGoals(event.getHomeScore());

        Match saved = matchRepository.save(match);

        afterCommitHandler.run(() -> lifecycleManager.handleMatchFinished(saved));
    }
}

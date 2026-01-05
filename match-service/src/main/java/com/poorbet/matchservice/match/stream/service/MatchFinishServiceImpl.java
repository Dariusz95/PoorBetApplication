package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.poorbet.matchservice.match.stream.model.enums.MatchStatus.FINISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchFinishServiceImpl implements MatchFinishService {

    private final MatchRepository matchRepository;

    @Transactional
    public void finishMatch(LiveMatchEventDto event) {
        log.info("🚀 finishMatch {}", event);

        Match match = matchRepository
                .findById(event.getId())
                .orElseThrow(() -> new IllegalStateException("Match not found: " + event.getId()));

        match.setStatus(FINISHED);
        match.setHomeGoals(event.getHomeScore());
        match.setAwayGoals(event.getAwayScore());

        matchRepository.save(match);
    }
}

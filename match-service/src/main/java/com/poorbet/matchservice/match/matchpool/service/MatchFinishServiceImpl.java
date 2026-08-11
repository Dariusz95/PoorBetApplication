package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.infrastructure.AfterCommitHandler;
import com.poorbet.matchservice.team.client.wallet.WalletClient;
import com.poorbet.matchservice.team.dto.CreditWalletRequest;
import com.poorbet.matchservice.team.model.Team;
import com.poorbet.matchservice.team.repository.TeamRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.poorbet.matchservice.match.match.domain.MatchStatus.FINISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchFinishServiceImpl implements MatchFinishService {

    private static final BigDecimal MATCH_WIN_COIN_REWARD = BigDecimal.ONE;

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final MatchPoolLifecycleManager lifecycleManager;
    private final WalletClient walletClient;
    private final AfterCommitHandler afterCommitHandler;

    @Transactional
    public void finishMatch(LiveMatchEventDto event) {
        log.info("🚀 finishMatch {}", event);

        Match match = matchRepository
                .findById(event.getId())
                .orElseThrow(() -> new IllegalStateException("Match not found: " + event.getId()));

        match.setStatus(FINISHED);
        match.setHomeGoals(event.getHomeScore());
        match.setAwayGoals(event.getAwayScore());

        Match saved = matchRepository.save(match);

        afterCommitHandler.run(() -> lifecycleManager.handleMatchFinished(saved));
        afterCommitHandler.run(() -> rewardWinningTeamOwner(saved));
    }

    private void rewardWinningTeamOwner(Match match) {
        if (match.getHomeGoals() == match.getAwayGoals()) {
            return;
        }

        UUID winnerTeamId = match.getHomeGoals() > match.getAwayGoals()
                ? match.getHomeTeamId()
                : match.getAwayTeamId();

        teamRepository.findById(winnerTeamId)
                .map(Team::getUserId)
                .filter(Objects::nonNull)
                .ifPresent(userId -> {
                    log.info("Team {} owned by user {} won match {} — awarding {} coin", winnerTeamId, userId, match.getId(), MATCH_WIN_COIN_REWARD);
                    walletClient.credit(userId, new CreditWalletRequest(MATCH_WIN_COIN_REWARD));
                });
    }
}

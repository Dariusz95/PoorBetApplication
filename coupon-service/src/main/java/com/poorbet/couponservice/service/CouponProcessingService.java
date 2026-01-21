package com.poorbet.couponservice.service;

import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.dto.MatchResultDto;
import com.poorbet.couponservice.dto.MatchResultMapDto;
import com.poorbet.couponservice.model.enums.BetStatus;
import com.poorbet.couponservice.model.enums.BetType;
import com.poorbet.couponservice.projections.BetProjection;
import com.poorbet.couponservice.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponProcessingService {

    private final MatchClient matchClient;
    private final BetRepository betRepository;

    @Transactional
    public void processFinishedMatch(List<UUID> matchIds) {
        Map<UUID, MatchResultDto> matchResults = matchClient.getMatchResult(matchIds).getResults();

        List<BetProjection> bets = betRepository.findBetsByMatchIds(matchIds);

        for (BetProjection bet : bets) {
            BetStatus status = mapResultToBetStatus(bet.getBetType(), matchResults.get(bet.getMatchId()));
            betRepository.updateBetStatusById(bet.getId(), status);
        }
    }

    private BetStatus mapResultToBetStatus(BetType betType, MatchResultDto result) {
        if (result == null) {
            return BetStatus.PENDING;
        }

        return switch (betType) {
            case HOME_WIN -> result.getHomeScore() > result.getAwayScore() ? BetStatus.WON : BetStatus.LOST;
            case AWAY_WIN -> result.getAwayScore() > result.getHomeScore() ? BetStatus.WON : BetStatus.LOST;
            case DRAW -> result.getHomeScore() == result.getAwayScore() ? BetStatus.WON : BetStatus.LOST;
        };
    }
}

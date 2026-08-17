package com.poorbet.matchservice.match.match.service;

import com.poorbet.matchservice.match.match.domain.Odds;
import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.repository.OddsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OddsServiceImpl implements OddsService {

    private OddsRepository oddsRepository;

    @Override
    public Optional<BigDecimal> getOdds(UUID matchId, OddsType type) {
        return oddsRepository.findByMatchId(matchId).map(odds -> extract(odds, type));
    }

    private BigDecimal extract(Odds odds, OddsType type) {
        return switch (type) {
            case HOME_WIN -> odds.getHomeWin();
            case DRAW -> odds.getDraw();
            case AWAY_WIN -> odds.getAwayWin();
            case OVER_2_5 -> odds.getOver25();
            case UNDER_2_5 -> odds.getUnder25();
            case OVER_3_5 -> odds.getOver35();
            case UNDER_3_5 -> odds.getUnder35();
        };
    }
}

package com.poorbet.matchservice.match.match.service;

import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.projections.AwayWin;
import com.poorbet.matchservice.match.match.projections.Draw;
import com.poorbet.matchservice.match.match.projections.HomeWin;
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
        return switch (type) {
            case HOME_WIN -> oddsRepository.findHomeWinByMatchId(matchId).map(HomeWin::getHomeWin);
            case DRAW -> oddsRepository.findDrawByMatchId(matchId).map(Draw::getDraw);
            case AWAY_WIN -> oddsRepository.findAwayWinByMatchId(matchId).map(AwayWin::getAwayWin);
        };
    }
}

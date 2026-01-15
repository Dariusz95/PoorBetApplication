package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.enums.OddsType;
import com.poorbet.matchservice.match.stream.projections.AwayWin;
import com.poorbet.matchservice.match.stream.projections.Draw;
import com.poorbet.matchservice.match.stream.projections.HomeWin;
import com.poorbet.matchservice.match.stream.repository.OddsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OddsServiceImpl implements OddsService{

    private OddsRepository oddsRepository;

    @Override
    public Optional<Double> getOdds(UUID matchId, OddsType type) {
        return switch (type){
            case HOME_WIN -> oddsRepository.findHomeWinByMatchId(matchId).map(HomeWin::getHomeWin);
            case DRAW -> oddsRepository.findDrawByMatchId(matchId).map(Draw::getDraw);
            case AWAY_WIN -> oddsRepository.findAwayWinByMatchId(matchId).map(AwayWin::getAwayWin);
        };
    }
}

package com.poorbet.matchservice.match.match.repository;

import com.poorbet.matchservice.match.match.domain.Odds;
import com.poorbet.matchservice.match.match.projections.AwayWin;
import com.poorbet.matchservice.match.match.projections.Draw;
import com.poorbet.matchservice.match.match.projections.HomeWin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OddsRepository extends JpaRepository<Odds, UUID> {

    Optional<HomeWin> findHomeWinByMatchId(UUID matchId);

    Optional<Draw> findDrawByMatchId(UUID matchId);

    Optional<AwayWin> findAwayWinByMatchId(UUID matchId);
}

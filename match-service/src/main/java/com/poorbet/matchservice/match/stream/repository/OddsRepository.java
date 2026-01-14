package com.poorbet.matchservice.match.stream.repository;

import com.poorbet.matchservice.match.stream.model.Odds;
import com.poorbet.matchservice.match.stream.projections.AwayWin;
import com.poorbet.matchservice.match.stream.projections.Draw;
import com.poorbet.matchservice.match.stream.projections.HomeWin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OddsRepository extends JpaRepository<Odds, UUID> {

    Optional<HomeWin> findHomeWinByMatchId(UUID matchId);

    Optional<Draw> findDrawByMatchId(UUID matchId);

    Optional<AwayWin> findAwayWinByMatchId(UUID matchId);
}

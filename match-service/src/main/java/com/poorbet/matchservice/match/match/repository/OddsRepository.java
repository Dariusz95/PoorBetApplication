package com.poorbet.matchservice.match.match.repository;

import com.poorbet.matchservice.match.match.domain.Odds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OddsRepository extends JpaRepository<Odds, UUID> {

    Optional<Odds> findByMatchId(UUID matchId);
}

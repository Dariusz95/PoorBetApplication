package com.poorbet.matchservice.match.stream.repository;

import com.poorbet.matchservice.match.stream.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {

    List<Match> findByPoolId(UUID poolId);
}

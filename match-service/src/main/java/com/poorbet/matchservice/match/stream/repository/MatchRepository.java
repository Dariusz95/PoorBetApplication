package com.poorbet.matchservice.match.stream.repository;

import com.poorbet.matchservice.match.stream.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
}

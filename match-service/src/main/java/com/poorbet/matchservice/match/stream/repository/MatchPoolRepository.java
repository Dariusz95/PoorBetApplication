package com.poorbet.matchservice.match.stream.repository;

import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.PoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchPoolRepository extends JpaRepository<MatchPool, UUID> {
    List<MatchPool> findByStatus(PoolStatus status);
}

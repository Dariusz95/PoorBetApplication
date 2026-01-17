package com.poorbet.matchservice.match.stream.repository;

import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {

    List<Match> findByPoolId(UUID poolId);

    @EntityGraph("matches")
    @Query("""
        select mp from MatchPool mp
        where mp.status =:status
        order by mp.scheduledStartTime asc
    """)
    List<MatchPool> getFutureMatchPools(@Param("status") PoolStatus  status, Pageable pageable);

    long countByPoolIdAndStatusNot(UUID matchId, MatchStatus status);
}

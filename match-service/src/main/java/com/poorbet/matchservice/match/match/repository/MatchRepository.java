package com.poorbet.matchservice.match.match.repository;

import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
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

    long countByPoolIdAndStatus(UUID matchId, MatchStatus status);

    @Query("""
        select new com.poorbet.matchservice.match.match.dto.MatchResultDto(
            m.id,
            m.homeGoals,
            m.awayGoals,
            m.status
        )
        from Match m
        where m.id in :ids
    """)
    List<MatchResultDto> findResultsByIds(@Param("ids") List<UUID> ids);
}

package com.poorbet.matchservice.match.matchpool.repository;

import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MatchPoolRepository extends JpaRepository<MatchPool, UUID> {

    @Query("""
                    select p from MatchPool p
                    where p.status = :status
                    and p.scheduledStartTime > :now
                    order by p.scheduledStartTime
            """)
    List<MatchPool> findFuturePools(@Param("status") PoolStatus status,
                                    @Param("now") OffsetDateTime now);

    @EntityGraph(attributePaths = {"matches", "matches.odds"})
    @Query("""
                select mp from MatchPool mp
                where mp.status = com.poorbet.matchservice.match.matchpool.domain.PoolStatus.BETTABLE
                order by mp.scheduledStartTime asc
            """)
    List<MatchPool> getFutureMatchPools(Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE MatchPool mp
            SET mp.status = :status
            WHERE mp.id = :id
            """)
    int updateStatus(UUID id, PoolStatus status);

    @Query("""
                select new com.poorbet.matchservice.match.match.dto.MatchResultDto(
                    m.id,
                    m.homeGoals,
                    m.awayGoals,
                    m.status
                )
                from Match m
                where m.pool.id = :poolId
            """)
    List<MatchResultDto> getResults(@Param("poolId") UUID poolId);
}
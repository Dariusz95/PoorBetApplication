package com.poorbet.matchservice.match.stream.repository;

import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.PoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

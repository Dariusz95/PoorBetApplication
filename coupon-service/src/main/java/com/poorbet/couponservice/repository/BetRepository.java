package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.model.Bet;
import com.poorbet.couponservice.model.enums.BetStatus;
import com.poorbet.couponservice.projections.BetProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BetRepository  extends JpaRepository<Bet, UUID> {

    @Modifying
    @Query("update Bet b set b.status = :status where b.id = :betId")
    void updateBetStatusById(UUID betId, BetStatus status);

    @Query("""
        select b.id as id, b.matchId as matchId, b.betType as betType
        from Bet b
        where b.matchId in :matchIds
    """)
    List<BetProjection> findBetsByMatchIds(List<UUID> matchIds);
}

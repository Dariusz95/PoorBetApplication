package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.projections.BetProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BetRepository  extends JpaRepository<Bet, UUID> {

    List<Bet> findAllByMatchIdIn(Collection<UUID> matchIds);
}

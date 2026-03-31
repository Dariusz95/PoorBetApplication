package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.domain.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BetRepository extends JpaRepository<Bet, UUID> {

    List<Bet> findAllByMatchIdIn(Collection<UUID> matchIds);
}

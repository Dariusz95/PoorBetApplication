package com.poorbet.couponservice.service;

import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.domain.*;
import com.poorbet.couponservice.dto.MatchResultDto;
import com.poorbet.couponservice.dto.MatchesFinishedEvent;
import com.poorbet.couponservice.projections.BetProjection;
import com.poorbet.couponservice.repository.BetRepository;
import com.poorbet.couponservice.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponProcessingService {

    private final BetRepository betRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public void processFinishedMatch(MatchesFinishedEvent event) {

        Map<UUID, MatchResultDto> matchResults  = event.results().stream()
                .collect(Collectors.toMap(
                        MatchResultDto::getId,
                        Function.identity()
                ));

        List<Bet> bets = betRepository.findAllByMatchIdIn(matchResults.keySet());

        bets.forEach(bet -> {
            MatchResultDto result = matchResults.get(bet.getMatchId());
            bet.setStatus(bet.getBetType().mapToStatus(result, result.getHomeGoals(), result.getAwayGoals()));
        });

        Set<UUID> couponIds = bets.stream()
                .map(Bet::getCoupon)
                .map(Coupon::getId)
                .collect(Collectors.toSet());

        List<Coupon> coupons = couponRepository.findAllWithBetsByIds(couponIds);

        coupons.forEach(this::updateCouponStatus);
    }

    public void updateCouponStatus(Coupon coupon) {
        boolean hasLost = coupon.getBets().stream()
                .anyMatch(bet -> bet.getStatus() == BetStatus.LOST);

        boolean allWon = coupon.getBets().stream()
                .allMatch(bet -> bet.getStatus() == BetStatus.WON);

        if (hasLost) {
            coupon.setStatus(CouponStatus.LOST);
        } else if (allWon) {
            coupon.setStatus(CouponStatus.WON);
        }
    }
}


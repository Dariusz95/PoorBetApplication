package com.poorbet.couponservice.service;

import com.poorbet.commons.rabbit.events.match.MatchesFinishedEvent;
import com.poorbet.commons.rabbit.events.match.dto.MatchResultEventDto;
import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.repository.BetRepository;
import com.poorbet.couponservice.repository.CouponRepository;
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

        Map<UUID, MatchResultEventDto> matchResults = event.results().stream()
                .collect(Collectors.toMap(
                        MatchResultEventDto::matchId,
                        Function.identity()
                ));

        List<Bet> bets = betRepository.findAllByMatchIdIn(matchResults.keySet());

        bets.forEach(bet -> {
            MatchResultEventDto result = matchResults.get(bet.getMatchId());
            bet.setStatus(bet.getBetType().mapToStatus(result, result.homeGoals(), result.awayGoals()));
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


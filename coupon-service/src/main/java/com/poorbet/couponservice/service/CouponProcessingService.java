package com.poorbet.couponservice.service;

import com.poorbet.commons.rabbit.events.coupon.CouponEvents;
import com.poorbet.commons.rabbit.events.coupon.CouponLostEvent;
import com.poorbet.commons.rabbit.events.coupon.CouponWonEvent;
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
    private final OutboxService outboxService;

    @Transactional
    public void processFinishedMatch(MatchesFinishedEvent event) {

        Map<UUID, MatchResultEventDto> matchResults = event.results().stream()
                .collect(Collectors.toMap(
                        MatchResultEventDto::matchId,
                        Function.identity()
                ));

        List<Bet> bets = betRepository.findAllByMatchIdIn(matchResults.keySet());

        List<Bet> pendingBets = bets.stream()
                .filter(bet -> bet.getStatus() == BetStatus.PENDING)
                .toList();

        pendingBets.forEach(bet -> {
            MatchResultEventDto result = matchResults.get(bet.getMatchId());
            bet.settle(result);
        });

        Set<UUID> couponIds = pendingBets.stream()
                .map(Bet::getCoupon)
                .map(Coupon::getId)
                .collect(Collectors.toSet());

        if (couponIds.isEmpty()) {
            return;
        }

        List<Coupon> coupons = couponRepository.findAllWithBetsByIds(couponIds);

        coupons.forEach(coupon -> {
            CouponStatus previousStatus = coupon.getStatus();

            coupon.recalculateStatus();

            if (previousStatus != coupon.getStatus()) {

                if (coupon.getStatus() == CouponStatus.WON) {
                    CouponWonEvent wonEvent = new CouponWonEvent(coupon.getId(),
                            coupon.getReservationId(),
                            coupon.getUserId(),
                            coupon.getPotentialPayout()
                    );

                    outboxService.saveEvent(CouponEvents.COUPON_WON, wonEvent);
                }

                if (coupon.getStatus() == CouponStatus.LOST) {
                    CouponLostEvent lostEvent = new CouponLostEvent(coupon.getId());

                    outboxService.saveEvent(CouponEvents.COUPON_LOST, lostEvent);
                }
            }
        });
    }
}


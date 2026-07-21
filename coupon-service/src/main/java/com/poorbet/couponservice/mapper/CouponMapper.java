package com.poorbet.couponservice.mapper;

import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CouponMapper {
    public CouponDto toDto(Coupon coupon){
        return new CouponDto(
                coupon.getId(),
                coupon.getStake(),
                coupon.getStatus(),
                coupon.getPotentialPayout(),
                coupon.getCreatedAt()
        );
    }

    public CouponDetailDto toDetailDto(Coupon coupon) {
        return new CouponDetailDto(
                coupon.getId(),
                coupon.getStake(),
                coupon.getStatus(),
                coupon.getPotentialPayout(),
                calculateTotalOdds(coupon),
                coupon.getCreatedAt(),
                coupon.getBets().stream()
                        .map(this::toBetDto)
                        .toList()
        );
    }

    private BetDto toBetDto(Bet bet) {
        return new BetDto(
                bet.getId(),
                bet.getMatchId(),
                bet.getHomeTeamName(),
                bet.getAwayTeamName(),
                bet.getMatchStartTime(),
                bet.getStatus(),
                bet.getBetType(),
                bet.getOdds(),
                bet.getHomeGoals(),
                bet.getAwayGoals()
        );
    }

    private BigDecimal calculateTotalOdds(Coupon coupon) {
        return coupon.getBets().stream()
                .map(Bet::getOdds)
                .reduce(BigDecimal.ONE, BigDecimal::multiply);
    }

    public RankingCouponDto toRankingCouponDto(Coupon coupon) {
        return new RankingCouponDto(
                coupon.getId(),
                coupon.getUserId(),
                coupon.getStake(),
                coupon.getStatus(),
                coupon.getPotentialPayout(),
                coupon.getCreatedAt(),
                coupon.getTotalOdds()
        );
    }

    public RankingCouponResponseDto toRankingCouponResponseDto(RankingCouponDto coupon, String email) {
        return new RankingCouponResponseDto(
                coupon.couponId(),
                coupon.stake(),
                email,
                coupon.status(),
                coupon.potentialPayout(),
                coupon.createdAt(),
                coupon.totalOdds()
        );
    }
}

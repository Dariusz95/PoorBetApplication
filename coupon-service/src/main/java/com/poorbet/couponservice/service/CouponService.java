package com.poorbet.couponservice.service;

import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.domain.OddsType;
import com.poorbet.couponservice.repository.CouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MatchClient matchClient;

    @Transactional
    public Coupon createCoupon(CreateCouponDto dto, UUID userId) {
        Coupon coupon = Coupon.builder()
                .stake(dto.getStake())
                .status(CouponStatus.OPEN)
                .build();

        dto.getBets().forEach(betDto -> {

            Double odd = matchClient.getOdd(
                    betDto.getMatchId(),
                    OddsType.HOME_WIN
            );

            Bet bet = Bet.builder()
                    .betType(betDto.getBetType())
                    .matchId(betDto.getMatchId())
                    .odds(BigDecimal.valueOf(odd))
                    .status(BetStatus.PENDING)
                    .build();

            coupon.addBet(bet);

        });

        return couponRepository.save(coupon);
    }


}

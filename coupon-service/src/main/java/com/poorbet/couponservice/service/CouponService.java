package com.poorbet.couponservice.service;

import com.poorbet.couponservice.dto.CreateBetDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.model.Bet;
import com.poorbet.couponservice.model.Coupon;
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

    @Transactional
    public Coupon createCoupon(CreateCouponDto dto, UUID userId){
        Coupon coupon = Coupon.builder()
                .stake(dto.getStake())
                .build();

        dto.getBets().forEach(betDto->{
            Bet bet = Bet.builder()
                    .betType(betDto.getBetType())
                    .matchId(betDto.getMatchId())
                    .odds(BigDecimal.valueOf(1.20))
                    .build();

            coupon.addBet(bet);

        });

        return couponRepository.save(coupon);
    }
}

package com.poorbet.couponservice.service;

import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.client.wallet.WalletBusinessException;
import com.poorbet.couponservice.client.wallet.WalletClient;
import com.poorbet.couponservice.client.wallet.WalletTechnicalException;
import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CreateCouponDto;
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
    private final WalletClient walletClient;

    @Transactional
    public Coupon createCoupon(CreateCouponDto dto, UUID userId) {

        UUID reservationId = UUID.randomUUID();

        try {
            walletClient.reserve(
                    userId,
                    new ReserveRequest(reservationId, dto.getStake())
            );

            Coupon coupon = buildCoupon(dto, userId, reservationId);
            dto.getBets().forEach(betDto -> {

                Double odd = matchClient.getOdd(
                        betDto.getMatchId(),
                        betDto.getBetType()
                );

                Bet bet = Bet.builder()
                        .betType(betDto.getBetType())
                        .matchId(betDto.getMatchId())
                        .odds(BigDecimal.valueOf(odd))
                        .status(BetStatus.PENDING)
                        .build();

                coupon.addBet(bet);
            });

            Coupon saved = couponRepository.save(coupon);

            return saved;

        } catch (WalletBusinessException ex) {
            throw ex;

        } catch (WalletTechnicalException ex) {


            //TODO dodanie outbox + obsluga w wallecie
//            outboxService.saveEvent(
//                    "CouponCreationFailed",
//                    new CouponCreationFailedEvent(userId, reservationId)
//            );

            throw ex;
        }
    }

    private Coupon buildCoupon(CreateCouponDto dto, UUID userId, UUID reservationId) {
        return Coupon.builder()
                .stake(dto.getStake())
                .userId(userId)
                .status(CouponStatus.OPEN)
                .build();
    }
}

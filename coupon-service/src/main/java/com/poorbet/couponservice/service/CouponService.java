package com.poorbet.couponservice.service;

import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.commons.rabbit.events.coupon.CouponCreationFailedEvent;
import com.poorbet.commons.rabbit.events.coupon.CouponEvents;
import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.client.wallet.WalletBusinessException;
import com.poorbet.couponservice.client.wallet.WalletClient;
import com.poorbet.couponservice.client.wallet.WalletTechnicalException;
import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CouponDetailDto;
import com.poorbet.couponservice.dto.CouponDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.mapper.CouponMapper;
import com.poorbet.couponservice.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MatchClient matchClient;
    private final WalletClient walletClient;
    private final OutboxService outboxService;
    private final CouponMapper couponMapper;

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

            BigDecimal totalOdds = coupon.getBets().stream()
                    .map(Bet::getOdds)
                    .reduce(BigDecimal.ONE, BigDecimal::multiply);

            BigDecimal potentialPayout = dto.getStake().multiply(totalOdds);

            coupon.setPotentialPayout(potentialPayout);

            Coupon saved = couponRepository.save(coupon);

            return saved;

        } catch (WalletBusinessException ex) {
            throw ex;

        } catch (WalletTechnicalException ex) {
            outboxService.saveEvent(
                    CouponEvents.COUPON_CREATION_FAILED,
                    new CouponCreationFailedEvent(reservationId)
            );

            throw ex;
        }
    }

    private Coupon buildCoupon(CreateCouponDto dto, UUID userId, UUID reservationId) {
        return Coupon.builder()
                .stake(dto.getStake())
                .userId(userId)
                .reservationId(reservationId)
                .status(CouponStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Page<CouponDto> getCoupons(UUID userId, CouponStatus status, Pageable pageable) {
        return couponRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(couponMapper::toDto);
    }

    public CouponDetailDto getCouponDetails(UUID couponId) {
        return this.couponRepository.findById(couponId)
                .map(couponMapper::toDetailDto)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
    }
}

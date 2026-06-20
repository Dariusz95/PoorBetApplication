package com.poorbet.couponservice.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MatchClient matchClient;
    private final WalletClient walletClient;
    private final OutboxService outboxService;
    private final CouponMapper couponMapper;

    @Transactional
    public CouponDetailDto createCoupon(CreateCouponDto dto, UUID userId) {

        UUID reservationId = UUID.randomUUID();

        try {
            walletClient.reserve(
                    userId,
                    new ReserveRequest(reservationId, dto.getStake())
            );

            Coupon coupon = buildCoupon(dto, userId, reservationId);
            dto.getBets().forEach(betDto -> {

                var snapshot = matchClient.getBetSnapshot(
                        betDto.getMatchId(),
                        betDto.getBetType()
                );

                Bet bet = Bet.builder()
                        .betType(betDto.getBetType())
                        .matchId(snapshot.matchId())
                        .homeTeamName(snapshot.homeTeamName())
                        .awayTeamName(snapshot.awayTeamName())
                        .matchStartTime(snapshot.matchStartTime())
                        .odds(snapshot.odd())
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

            return couponMapper.toDetailDto(saved);

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
                .createdAt(OffsetDateTime.now())
                .build();
    }

    public Page<CouponDto> getMyCouponsByStatus(UUID userId, CouponStatus status, Pageable pageable) {
        return couponRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(couponMapper::toDto);
    }

    public Page<CouponDto> getMyCouponsByStatuses(UUID userId, List<CouponStatus> status, Pageable pageable) {
        return couponRepository.findByUserIdAndStatusIn(userId, status, pageable)
                .map(couponMapper::toDto);
    }

    public CouponDetailDto getCouponDetails(UUID couponId) {
        return this.couponRepository.findById(couponId)
                .map(couponMapper::toDetailDto)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
    }
}

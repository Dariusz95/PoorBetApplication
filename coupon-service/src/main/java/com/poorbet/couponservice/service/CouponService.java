package com.poorbet.couponservice.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.poorbet.commons.commons.pagination.PageResponse;
import com.poorbet.couponservice.filter.CouponFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.commons.rabbit.events.coupon.CouponCreationFailedEvent;
import com.poorbet.commons.rabbit.events.coupon.CouponEvents;
import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.client.wallet.WalletBusinessException;
import com.poorbet.couponservice.client.wallet.WalletClient;
import com.poorbet.couponservice.domain.Bet;
import com.poorbet.couponservice.domain.BetStatus;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.BetSnapshotRequest;
import com.poorbet.couponservice.dto.CouponDetailDto;
import com.poorbet.couponservice.dto.CouponDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.dto.MatchBetSnapshotDto;
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

            List<BetSnapshotRequest> snapshotRequests = dto.getBets().stream()
                    .map(b -> new BetSnapshotRequest(b.getMatchId(), b.getBetType()))
                    .toList();

            Map<UUID, MatchBetSnapshotDto> snapshots = matchClient.getBetSnapshots(snapshotRequests)
                    .stream()
                    .collect(Collectors.toMap(MatchBetSnapshotDto::matchId, Function.identity()));

            Coupon coupon = buildCoupon(dto, userId, reservationId);
            dto.getBets().forEach(betDto -> {
                MatchBetSnapshotDto snapshot = snapshots.get(betDto.getMatchId());
                if (snapshot == null) {
                    throw new IllegalStateException("Brak snapshotu dla meczu: " + betDto.getMatchId());
                }

                coupon.addBet(Bet.builder()
                        .betType(betDto.getBetType())
                        .matchId(snapshot.matchId())
                        .homeTeamName(snapshot.homeTeamName())
                        .awayTeamName(snapshot.awayTeamName())
                        .matchStartTime(snapshot.matchStartTime())
                        .odds(snapshot.odd())
                        .status(BetStatus.PENDING)
                        .build());
            });

            BigDecimal totalOdds = coupon.getBets().stream()
                    .map(Bet::getOdds)
                    .reduce(BigDecimal.ONE, BigDecimal::multiply);

            coupon.setPotentialPayout(dto.getStake().multiply(totalOdds));

            return couponMapper.toDetailDto(couponRepository.save(coupon));

        } catch (WalletBusinessException ex) {
            throw ex;

        } catch (Exception ex) {

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


    public PageResponse<CouponDto> getMyCoupons(
            UUID userId,
            CouponFilter filter,
            Pageable pageable
    ) {
        Page<CouponDto> page = couponRepository
                .findByUserIdAndStatusIn(userId, filter.getStatuses(), pageable)
                .map(couponMapper::toDto);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public CouponDetailDto getCouponDetails(UUID couponId) {
        return this.couponRepository.findById(couponId)
                .map(couponMapper::toDetailDto)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
    }
}

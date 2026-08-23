package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CouponLiveViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query("""
            select c from Coupon c join fetch c.bets where c.id in :couponIds
            """)
    List<Coupon> findAllWithBetsByIds(@Param("couponIds") Collection<UUID> couponIds);

    Page<Coupon> findByUserIdAndStatusIn(
            UUID userId,
            List<CouponStatus> statuses,
            Pageable pageable
    );

    Page<Coupon> findByStatus(CouponStatus status, Pageable pageable);

    @Query("""
            select distinct new com.poorbet.couponservice.dto.CouponLiveViewDto(
                c.id, b.matchId, c.totalOdds, c.potentialPayout
            )
            from Coupon c join c.bets b
            where c.userId = :userId and c.status = :status and b.matchId in :matchIds
            """)
    List<CouponLiveViewDto> findLiveViewsByUserAndMatchIds(
            @Param("userId") UUID userId,
            @Param("status") CouponStatus status,
            @Param("matchIds") Collection<UUID> matchIds
    );
}

package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
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

    Page<Coupon> findByUserIdAndStatus(UUID userId, CouponStatus status, Pageable pageable);
}

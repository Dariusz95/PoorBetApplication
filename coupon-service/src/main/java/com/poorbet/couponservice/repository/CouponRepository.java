package com.poorbet.couponservice.repository;

import com.poorbet.couponservice.model.Coupon;
import com.poorbet.couponservice.model.enums.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
}

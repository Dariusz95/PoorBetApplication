package com.poorbet.couponservice.controller;

import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.security.CurrentUserProvider;
import com.poorbet.couponservice.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.poorbet.commons.security.PoorbetPermissions).COUPON_CREATE)")
    public ResponseEntity<Coupon> createCoupon(@RequestBody @Valid CreateCouponDto createCouponDto) {
        Coupon coupon = couponService.createCoupon(createCouponDto, currentUserProvider.getUserId());
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }
}

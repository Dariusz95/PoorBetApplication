package com.poorbet.couponservice.controller;

import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CouponDetailDto;
import com.poorbet.couponservice.dto.CouponDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.security.CurrentUserProvider;
import com.poorbet.couponservice.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.poorbet.authstarter.security.PoorbetPermissions).COUPON_CREATE)")
    public ResponseEntity<CouponDetailDto> createCoupon(@RequestBody @Valid CreateCouponDto createCouponDto) {
        CouponDetailDto coupon = couponService.createCoupon(createCouponDto, currentUserProvider.getUserId());
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }

    @GetMapping("/me/open")
    public Page<CouponDto> getMyOpenCoupons(Pageable pageable) {
        return couponService.getMyCouponsByStatus(currentUserProvider.getUserId(), CouponStatus.OPEN, pageable);
    }

    @GetMapping("/me/won")
    public Page<CouponDto> getMyWonCoupons(Pageable pageable) {
        return couponService.getMyCouponsByStatus(currentUserProvider.getUserId(), CouponStatus.WON, pageable);
    }

    @GetMapping("/me/settled")
    public Page<CouponDto> getMySettledCoupons(Pageable pageable) {
        return couponService.getMyCouponsByStatuses(currentUserProvider.getUserId(), List.of(CouponStatus.WON, CouponStatus.LOST), pageable);
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<CouponDetailDto> getCouponDetails(@PathVariable UUID couponId) {
        return ResponseEntity.ok(couponService.getCouponDetails(couponId));
    }
}

package com.poorbet.couponservice.controller;

import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.service.CouponService;
import com.poorbet.couponservice.util.UserIdResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.poorbet.commons.security.PoorbetPermissions).COUPON_CREATE)")
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody @Valid CreateCouponDto createCouponDto,
            Authentication authentication

    ) {
        String subject = authentication != null ? authentication.getName() : "anonymous";
        Coupon coupon = couponService.createCoupon(createCouponDto, UserIdResolver.fromSubject(subject));
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }
}

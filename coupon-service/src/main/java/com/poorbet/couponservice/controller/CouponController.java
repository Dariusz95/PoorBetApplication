package com.poorbet.couponservice.controller;

import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.model.Coupon;
import com.poorbet.couponservice.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody @Valid CreateCouponDto createCouponDto

    ) {
        Coupon coupon = couponService.createCoupon(createCouponDto, UUID.randomUUID());
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }


}
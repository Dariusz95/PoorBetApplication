package com.poorbet.couponservice.controller;

import com.poorbet.couponservice.dto.CouponDetailDto;
import com.poorbet.couponservice.dto.RankingResponseDto;
import com.poorbet.couponservice.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/coupons/public")
@RequiredArgsConstructor
public class CouponPublicController {

    private final CouponService couponService;

    @GetMapping("/ranking/total-odds")
    public RankingResponseDto getHighestTotalOdds() {
        return couponService.getHighestTotalOdds();
    }

    @GetMapping("/ranking/payout")
    public RankingResponseDto getHighestPotentialPayout() {
        return couponService.getHighestPotentialPayout();
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<CouponDetailDto> getCouponDetails(@PathVariable UUID couponId) {
        return ResponseEntity.ok(couponService.getCouponDetails(couponId));
    }
}

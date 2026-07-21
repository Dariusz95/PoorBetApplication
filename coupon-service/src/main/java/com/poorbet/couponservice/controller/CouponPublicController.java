package com.poorbet.couponservice.controller;

import com.poorbet.commons.commons.pagination.PageResponse;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CouponDetailDto;
import com.poorbet.couponservice.dto.CouponDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.dto.RankingCouponResponseDto;
import com.poorbet.couponservice.filter.CouponFilter;
import com.poorbet.couponservice.security.CurrentUserProvider;
import com.poorbet.couponservice.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/coupons/public")
@RequiredArgsConstructor
public class CouponPublicController {

    private final CouponService couponService;

    @GetMapping("/ranking/total-odds")
    public PageResponse<RankingCouponResponseDto> getHighestTotalOdds() {
        return couponService.getHighestTotalOdds();
    }

    @GetMapping("/ranking/payout")
    public PageResponse<RankingCouponResponseDto> getHighestPotentialPayout() {
        return couponService.getHighestPotentialPayout();
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<CouponDetailDto> getCouponDetails(@PathVariable UUID couponId) {
        return ResponseEntity.ok(couponService.getCouponDetails(couponId));
    }
}

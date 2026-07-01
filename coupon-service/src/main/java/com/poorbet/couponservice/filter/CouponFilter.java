package com.poorbet.couponservice.filter;

import com.poorbet.couponservice.domain.CouponStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CouponFilter {

    private List<CouponStatus> statuses;

}
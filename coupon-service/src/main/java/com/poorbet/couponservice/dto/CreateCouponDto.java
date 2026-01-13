package com.poorbet.couponservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateCouponDto {
    @NotNull(message = "Stake cannot be null")
    @DecimalMin(value = "1.00", message = "Stake must be at least 1.00")
    private BigDecimal stake;

    @NotEmpty(message = "Coupon must have at least one bet")
    @Valid
    private List<CreateBetDto> bets;
}

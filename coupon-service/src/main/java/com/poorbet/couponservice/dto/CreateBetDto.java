package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.model.enums.BetType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBetDto {
    @NotNull(message = "matchId cannot be null")
    private UUID matchId;

    @NotNull(message = "betType cannot be null")
    private BetType betType;
}

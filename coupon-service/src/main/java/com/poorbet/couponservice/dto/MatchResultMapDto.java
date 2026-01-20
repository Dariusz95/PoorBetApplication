package com.poorbet.couponservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MatchResultMapDto {
    private Map<Long, MatchResultDto> results;
}

package com.poorbet.couponservice.dto;

import java.util.List;

public record MatchesFinishedEvent(
        List<MatchResultDto> results
) {
}

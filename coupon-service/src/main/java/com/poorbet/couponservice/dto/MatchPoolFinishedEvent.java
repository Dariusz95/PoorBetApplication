package com.poorbet.couponservice.dto;

import java.util.UUID;

public record MatchPoolFinishedEvent(
        UUID matchPoolId
) {
}

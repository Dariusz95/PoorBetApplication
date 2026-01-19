package com.poorbet.couponservice.dto;

import java.util.List;
import java.util.UUID;

public record MatchPoolFinishedEvent(
        List<UUID> matchIds
) {
}

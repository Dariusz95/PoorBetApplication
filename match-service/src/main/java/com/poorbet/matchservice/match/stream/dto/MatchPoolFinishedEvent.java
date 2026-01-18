package com.poorbet.matchservice.match.stream.dto;

import java.util.UUID;

public record MatchPoolFinishedEvent(
        UUID matchPoolId
) {
}

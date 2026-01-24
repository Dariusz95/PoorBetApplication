package com.poorbet.matchservice.match.stream.dto;

import java.util.List;
import java.util.UUID;

public record MatchesFinishedEvent(
        List<MatchResultDto> results
) {
}

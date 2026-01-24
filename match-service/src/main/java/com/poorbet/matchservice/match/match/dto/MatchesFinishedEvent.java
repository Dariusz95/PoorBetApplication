package com.poorbet.matchservice.match.match.dto;

import java.util.List;

public record MatchesFinishedEvent(
        List<MatchResultDto> results
) {
}

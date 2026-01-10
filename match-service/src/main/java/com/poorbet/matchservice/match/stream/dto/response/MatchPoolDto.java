package com.poorbet.matchservice.match.stream.dto.response;

import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MatchPoolDto(
        UUID id,
        PoolStatus status,
        OffsetDateTime scheduledStartTime,
        List<MatchDto> matches
) {}

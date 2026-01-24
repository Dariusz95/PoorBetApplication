package com.poorbet.matchservice.match.matchpool.dto;

import com.poorbet.matchservice.match.match.dto.response.MatchDto;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MatchPoolDto(
        UUID id,
        PoolStatus status,
        OffsetDateTime scheduledStartTime,
        List<MatchDto> matches
) {}

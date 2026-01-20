package com.poorbet.matchservice.match.stream.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class MatchResultMapDto {
    private Map<UUID, MatchResultDto> results;
}

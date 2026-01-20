package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.stream.dto.response.MatchPoolDto;

import java.util.List;
import java.util.UUID;

public interface MatchService {
    List<MatchPoolDto> getFutureMatchPools();
}

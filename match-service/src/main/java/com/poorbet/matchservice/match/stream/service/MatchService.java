package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.response.MatchPoolDto;

import java.util.List;

public interface MatchService {
    List<MatchPoolDto> getFutureMatchPools();
}

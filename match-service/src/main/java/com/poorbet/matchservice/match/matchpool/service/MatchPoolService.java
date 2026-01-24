package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;

import java.util.List;
import java.util.UUID;

public interface MatchPoolService {

    void startPool(UUID poolId);

    List<MatchPoolDto> getFutureMatchPools();
}
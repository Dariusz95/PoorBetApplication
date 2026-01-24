package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;

public interface MatchFinishService {
    void finishMatch(LiveMatchEventDto event);
}

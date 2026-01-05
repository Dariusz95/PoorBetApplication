package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.LiveMatchEventDto;

public interface MatchFinishService {
    void finishMatch(LiveMatchEventDto event);
}

package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;

public interface MatchFinishService {
    void finishMatch(LiveMatchEvent event);
}

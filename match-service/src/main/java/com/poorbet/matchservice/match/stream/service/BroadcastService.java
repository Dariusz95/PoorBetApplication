package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.MatchUpdate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface BroadcastService {

    void addSubscriber(SseEmitter emitter);

    void removeSubscriber(SseEmitter emitter);

    void broadcast(MatchUpdate update);

    void broadcastAll(List<MatchUpdate> updates);

}
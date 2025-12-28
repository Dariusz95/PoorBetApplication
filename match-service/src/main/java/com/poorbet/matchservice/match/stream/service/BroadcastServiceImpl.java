package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.MatchUpdate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public class BroadcastServiceImpl implements BroadcastService{
    @Override
    public void addSubscriber(SseEmitter emitter) {

    }

    @Override
    public void removeSubscriber(SseEmitter emitter) {

    }

    @Override
    public void broadcast(MatchUpdate update) {

    }

    @Override
    public void broadcastAll(List<MatchUpdate> updates) {

    }
}
